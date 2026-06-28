package satm;

import satm.controller.ATMController;
import satm.model.Account;
import satm.model.Bank;
import satm.model.CashDispenser;
import satm.model.Customer;
import satm.model.DepositSlot;
import satm.model.WithdrawalPolicy;
import satm.view.ATMView;
import satm.view.ConsoleATMView;

public class Main {

    public static void main(String[] args) {
        // --- Model: the bank and its customer account file ---
        Bank bank = new Bank();
        // PAN, PIN, name, opening checking balance (whole dollars)
        bank.addCustomer(new Customer("1001", "2468", "Alice Walker", new Account("CHK-1001", 500)));
        bank.addCustomer(new Customer("1002", "1357", "Bob Stone",    new Account("CHK-1002", 75)));

        // --- Model: terminal resources (status comes from the control file) ---
        CashDispenser dispenser = new CashDispenser(2000, true); // $2000 loaded, chute operational
        DepositSlot depositSlot = new DepositSlot(true);         // deposit slot available
        WithdrawalPolicy policy  = new WithdrawalPolicy();

        // --- View ---
        ATMView view = new ConsoleATMView();

        // --- Controller: the finite-state machine driving the screens ---
        ATMController controller = new ATMController(view, bank, dispenser, depositSlot, policy);
        controller.run();
    }
}
