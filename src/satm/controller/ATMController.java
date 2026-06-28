package satm.controller;

import satm.model.Account;
import satm.model.BalanceInquiry;
import satm.model.Bank;
import satm.model.CashDispenser;
import satm.model.Customer;
import satm.model.Deposit;
import satm.model.DepositSlot;
import satm.model.TransactionType;
import satm.model.TxnResult;
import satm.model.Withdrawal;
import satm.model.WithdrawalPolicy;
import satm.view.ATMView;
import satm.view.EndOfInputException;
import satm.view.Screen;


public class ATMController {

    private static final int MAX_PIN_ATTEMPTS = 3;

    private final ATMView view;
    private final Bank bank;
    private final CashDispenser dispenser;
    private final DepositSlot depositSlot;
    private final WithdrawalPolicy withdrawalPolicy;

    private Session session;

    public ATMController(ATMView view, Bank bank, CashDispenser dispenser,
                         DepositSlot depositSlot, WithdrawalPolicy withdrawalPolicy) {
        this.view = view;
        this.bank = bank;
        this.dispenser = dispenser;
        this.depositSlot = depositSlot;
        this.withdrawalPolicy = withdrawalPolicy;
    }

    /** Main loop: serve one customer after another until input is exhausted. */
    public void run() {
        view.showMessage("=== SATM Simulator — MM Federal Credit Union ===");
        try {
            while (true) {
                session = new Session();
                view.display(Screen.SCREEN1_WELCOME);
                String pan = view.readCard();
                if (pan == null || pan.isBlank() || pan.equalsIgnoreCase("quit")) {
                    break;
                }
                if (!cardEntry(pan)) {     // SUC1 / SUC2
                    continue;
                }
                if (!pinEntry()) {         // SUC3 / SUC4
                    continue;
                }
                transactionLoop();         // SUC5..SUC12
            }
        } catch (EndOfInputException eof) {
            // input stream ended mid-flow: fall through to shut down cleanly
        }
        view.showMessage("Shutting down. Goodbye.");
    }

    /** SUC1 valid card / SUC2 invalid card. */
    private boolean cardEntry(String pan) {
        Customer customer = bank.authenticate(pan);
        if (customer == null) {
            view.display(Screen.SCREEN4_INVALID_CARD);
            view.retainCard();
            return false;
        }
        session.setCustomer(customer);
        return true;
    }

    /** SUC3 correct PIN / SUC4 failed PIN (three tries, then retain card). */
    private boolean pinEntry() {
        view.display(Screen.SCREEN2_ENTER_PIN);
        while (true) {
            String pin = view.readPin();
            if (session.getCustomer().verifyPin(pin)) {
                return true;
            }
            if (session.incrementPinAttempts() >= MAX_PIN_ATTEMPTS) {
                view.display(Screen.SCREEN4_INVALID_CARD);
                view.retainCard();
                return false;
            }
            view.display(Screen.SCREEN3_PIN_INCORRECT);
        }
    }

    /** Screen 5 transaction selection, repeated until the customer declines. */
    private void transactionLoop() {
        boolean another = true;
        while (another) {
            view.display(Screen.SCREEN5_SELECT_TXN);
            TransactionType type = view.readTransactionChoice();
            Screen terminal = switch (type) {
                case BALANCE    -> doBalance();
                case DEPOSIT    -> doDeposit();
                case WITHDRAWAL -> doWithdrawal();
            };
            view.display(terminal);                          // screen 10/12/14: "Another transaction?"
            another = view.readYesNo("Another transaction?"); // SUC12 = yes, SUC11 = no
        }
        view.display(Screen.SCREEN15_TAKE_CARD);
        view.ejectCard();
    }

    /** SUC5 — Choose Balance. */
    private Screen doBalance() {
        Account account = session.getCustomer().getAccount();
        TxnResult result = new BalanceInquiry(account).execute();
        view.display(Screen.SCREEN6_BALANCE);
        view.showBalance(result.getBalance());
        return Screen.SCREEN14_NEW_BALANCE;
    }

    /** SUC6 — Choose Deposit. */
    private Screen doDeposit() {
        Account account = session.getCustomer().getAccount();
        if (!depositSlot.isAvailable()) {
            return Screen.SCREEN12_NO_DEPOSIT;
        }
        view.display(Screen.SCREEN7_ENTER_AMOUNT);
        int amount = view.readAmount();
        view.display(Screen.SCREEN13_INSERT_DEPOSIT);
        TxnResult result = new Deposit(account, depositSlot, amount).execute();
        if (result.getOutcome() == satm.model.TxnOutcome.DEPOSIT_SLOT_UNAVAILABLE) {
            return Screen.SCREEN12_NO_DEPOSIT;
        }
        return Screen.SCREEN14_NEW_BALANCE;
    }

    /** SUC7–SUC10 — Choose Withdrawal, re-prompting on recoverable problems. */
    private Screen doWithdrawal() {
        Account account = session.getCustomer().getAccount();
        if (!dispenser.isOperational()) {                    // chute jammed
            return Screen.SCREEN10_NO_WITHDRAWAL;
        }
        while (true) {
            view.display(Screen.SCREEN7_ENTER_AMOUNT);
            int amount = view.readAmount();
            Withdrawal withdrawal = new Withdrawal(
                    account, dispenser, withdrawalPolicy, amount, session.getDailyWithdrawn());
            TxnResult result = withdrawal.execute();
            switch (result.getOutcome()) {
                case SUCCESS -> {
                    session.addWithdrawal(amount);
                    view.display(Screen.SCREEN11_TAKE_CASH);
                    view.dispenseCash(amount);
                    view.printReceipt(result.getBalance());
                    return Screen.SCREEN14_NEW_BALANCE;
                }
                case INVALID_MULTIPLE      -> view.display(Screen.SCREEN9_DENOMINATION);     // re-prompt
                case INSUFFICIENT_FUNDS    -> view.display(Screen.SCREEN8_INSUFFICIENT_FUNDS); // re-prompt
                case DAILY_LIMIT_EXCEEDED  -> view.showMessage(
                        "    >> Amount exceeds your daily withdrawal limit. Please enter a new amount.");
                case DISPENSER_UNAVAILABLE -> { return Screen.SCREEN10_NO_WITHDRAWAL; }
                default                    -> { return Screen.SCREEN10_NO_WITHDRAWAL; }
            }
        }
    }
}
