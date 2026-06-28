package satm.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import satm.model.Account;
import satm.model.Bank;
import satm.model.CashDispenser;
import satm.model.Customer;
import satm.model.DepositSlot;
import satm.model.TransactionType;
import satm.model.WithdrawalPolicy;
import satm.view.Screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ATMControllerTest {

    private Account account;   // reference to the logged-in customer's account

    /** Build a controller over one customer ("1001"/"2468") with given resources. */
    private ATMController build(FakeATMView view, int balance,
                                CashDispenser dispenser, DepositSlot slot) {
        account = new Account("CHK-1001", balance);
        Bank bank = new Bank();
        bank.addCustomer(new Customer("1001", "2468", "Alice", account));
        return new ATMController(view, bank, dispenser, slot, new WithdrawalPolicy());
    }

    private ATMController build(FakeATMView view, int balance) {
        return build(view, balance, new CashDispenser(2000, true), new DepositSlot(true));
    }

    // ===================== UC1 / UC2 : card entry =====================

    @Test
    @DisplayName("UC1 — valid card advances to the PIN screen")
    void uc1_validCard() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.BALANCE).yesNo(false);
        build(view, 500).run();

        assertTrue(view.displayed(Screen.SCREEN2_ENTER_PIN), "PIN screen shown after valid card");
        assertTrue(view.displayed(Screen.SCREEN5_SELECT_TXN));
        assertEquals(0, view.retainCount);
    }

    @Test
    @DisplayName("UC2 — invalid card is retained, no PIN prompt")
    void uc2_invalidCard() {
        FakeATMView view = new FakeATMView().cards("9999");
        build(view, 500).run();

        assertTrue(view.displayed(Screen.SCREEN4_INVALID_CARD));
        assertEquals(1, view.retainCount);
        assertFalse(view.displayed(Screen.SCREEN2_ENTER_PIN), "must not prompt for PIN");
    }

    // ===================== UC3 / UC4 : PIN entry =====================

    @Test
    @DisplayName("UC3 — correct PIN on first try reaches transaction menu")
    void uc3_correctPin() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.BALANCE).yesNo(false);
        build(view, 500).run();

        assertTrue(view.displayed(Screen.SCREEN5_SELECT_TXN));
        assertEquals(0, view.countOf(Screen.SCREEN3_PIN_INCORRECT));
    }

    @Test
    @DisplayName("UC4 — three wrong PINs retain the card")
    void uc4_threeWrongPins() {
        FakeATMView view = new FakeATMView().cards("1001").pins("0000", "1111", "2222");
        build(view, 500).run();

        assertEquals(2, view.countOf(Screen.SCREEN3_PIN_INCORRECT), "two retry screens before lockout");
        assertTrue(view.displayed(Screen.SCREEN4_INVALID_CARD));
        assertEquals(1, view.retainCount);
        assertFalse(view.displayed(Screen.SCREEN5_SELECT_TXN));
    }

    @Test
    @DisplayName("UC4 — two wrong PINs then a correct one succeeds")
    void uc4_recoversAfterRetries() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("0000", "1111", "2468")
                .choices(TransactionType.BALANCE).yesNo(false);
        build(view, 500).run();

        assertEquals(2, view.countOf(Screen.SCREEN3_PIN_INCORRECT));
        assertTrue(view.displayed(Screen.SCREEN5_SELECT_TXN));
        assertEquals(0, view.retainCount);
    }

    // ===================== UC5 : balance =====================

    @Test
    @DisplayName("UC5 — balance inquiry shows the balance")
    void uc5_balance() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.BALANCE).yesNo(false);
        build(view, 500).run();

        assertTrue(view.displayed(Screen.SCREEN6_BALANCE));
        assertTrue(view.balancesShown.contains(500));
        assertTrue(view.displayed(Screen.SCREEN14_NEW_BALANCE));
    }

    // ===================== UC6 : deposit =====================

    @Test
    @DisplayName("UC6 — deposit credits the account")
    void uc6_deposit() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.DEPOSIT).amounts(200).yesNo(false);
        build(view, 500).run();

        assertTrue(view.displayed(Screen.SCREEN13_INSERT_DEPOSIT));
        assertEquals(700, account.getBalance());
        assertTrue(view.displayed(Screen.SCREEN14_NEW_BALANCE));
    }

    @Test
    @DisplayName("UC6 — deposit refused when slot unavailable")
    void uc6_depositSlotUnavailable() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.DEPOSIT).yesNo(false);
        build(view, 500, new CashDispenser(2000, true), new DepositSlot(false)).run();

        assertTrue(view.displayed(Screen.SCREEN12_NO_DEPOSIT));
        assertFalse(view.displayed(Screen.SCREEN13_INSERT_DEPOSIT));
        assertEquals(500, account.getBalance(), "balance unchanged");
    }

    // ===================== UC7–UC10 : withdrawal =====================

    @Test
    @DisplayName("UC7 — valid withdrawal dispenses cash and debits the account")
    void uc7_withdrawalSuccess() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.WITHDRAWAL).amounts(100).yesNo(false);
        build(view, 500).run();

        assertTrue(view.displayed(Screen.SCREEN11_TAKE_CASH));
        assertTrue(view.cashDispensed.contains(100));
        assertTrue(view.receipts.contains(400));
        assertEquals(400, account.getBalance());
    }

    @Test
    @DisplayName("UC8 — non-$10 multiple is rejected, then a valid amount succeeds")
    void uc8_invalidMultipleThenValid() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.WITHDRAWAL)
                .amounts(25, 20).yesNo(false);
        build(view, 500).run();

        assertTrue(view.displayed(Screen.SCREEN9_DENOMINATION));
        assertTrue(view.cashDispensed.contains(20));
        assertEquals(480, account.getBalance());
    }

    @Test
    @DisplayName("UC9 — insufficient funds rejected, then a smaller amount succeeds")
    void uc9_insufficientFundsThenValid() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.WITHDRAWAL)
                .amounts(100, 40).yesNo(false);
        build(view, 50).run();

        assertTrue(view.displayed(Screen.SCREEN8_INSUFFICIENT_FUNDS));
        assertTrue(view.cashDispensed.contains(40));
        assertEquals(10, account.getBalance());
    }

    @Test
    @DisplayName("UC10 — over daily limit rejected, then a within-limit amount succeeds")
    void uc10_dailyLimitThenValid() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.WITHDRAWAL)
                .amounts(600, 200).yesNo(false);
        build(view, 2000, new CashDispenser(5000, true), new DepositSlot(true)).run();

        assertFalse(view.messages.isEmpty(), "daily-limit message surfaced");
        assertTrue(view.cashDispensed.contains(200));
        assertEquals(1800, account.getBalance());
    }

    @Test
    @DisplayName("Withdrawal blocked when chute is jammed (screen 10, no amount prompt)")
    void withdrawal_chuteJammed() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.WITHDRAWAL).yesNo(false);
        build(view, 500, new CashDispenser(2000, false), new DepositSlot(true)).run();

        assertTrue(view.displayed(Screen.SCREEN10_NO_WITHDRAWAL));
        assertFalse(view.displayed(Screen.SCREEN7_ENTER_AMOUNT));
        assertEquals(500, account.getBalance());
    }

    @Test
    @DisplayName("Withdrawal blocked when machine is low on cash (screen 10)")
    void withdrawal_machineLowOnCash() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.WITHDRAWAL).amounts(100).yesNo(false);
        build(view, 500, new CashDispenser(40, true), new DepositSlot(true)).run();

        assertTrue(view.displayed(Screen.SCREEN10_NO_WITHDRAWAL));
        assertEquals(500, account.getBalance(), "no debit when machine cannot dispense");
    }

    // ===================== UC11 / UC12 : session loop =====================

    @Test
    @DisplayName("UC11 — declining another transaction ejects the card")
    void uc11_noOtherTransaction() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468").choices(TransactionType.BALANCE).yesNo(false);
        build(view, 500).run();

        assertTrue(view.displayed(Screen.SCREEN15_TAKE_CARD));
        assertEquals(1, view.ejectCount);
    }

    @Test
    @DisplayName("UC12 — choosing another transaction returns to the menu")
    void uc12_anotherTransaction() {
        FakeATMView view = new FakeATMView()
                .cards("1001").pins("2468")
                .choices(TransactionType.BALANCE, TransactionType.BALANCE)
                .yesNo(true, false);
        build(view, 500).run();

        assertEquals(2, view.countOf(Screen.SCREEN5_SELECT_TXN), "menu shown twice");
        assertEquals(1, view.ejectCount);
    }

    // ===================== shutdown =====================

    @Test
    @DisplayName("Blank card entry shuts the machine down cleanly")
    void blankCardShutsDown() {
        FakeATMView view = new FakeATMView().cards("");
        build(view, 500).run();

        assertTrue(view.messages.stream().anyMatch(m -> m.toLowerCase().contains("shutting down")));
        assertFalse(view.displayed(Screen.SCREEN2_ENTER_PIN));
    }
}
