package satm.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TransactionTest {

    private final WithdrawalPolicy policy = new WithdrawalPolicy();

    // ---------- UC5: Balance ----------
    @Test
    void balanceInquiryReportsBalanceWithoutChangingIt() {
        Account a = new Account("CHK-1", 500);
        TxnResult r = new BalanceInquiry(a).execute();
        assertTrue(r.isSuccess());
        assertEquals(500, r.getBalance());
        assertEquals(500, a.getBalance());
    }

    // ---------- UC6: Deposit ----------
    @Test
    void depositCreditsAccountWhenSlotAvailable() {
        Account a = new Account("CHK-1", 100);
        TxnResult r = new Deposit(a, new DepositSlot(true), 250).execute();
        assertEquals(TxnOutcome.SUCCESS, r.getOutcome());
        assertEquals(350, r.getBalance());
        assertEquals(350, a.getBalance());
    }

    @Test
    void depositRejectedWhenSlotUnavailable() {
        Account a = new Account("CHK-1", 100);
        TxnResult r = new Deposit(a, new DepositSlot(false), 250).execute();
        assertEquals(TxnOutcome.DEPOSIT_SLOT_UNAVAILABLE, r.getOutcome());
        assertEquals(100, a.getBalance(), "balance untouched when slot unavailable");
    }

    // ---------- UC7: successful withdrawal ----------
    @Test
    void withdrawalSucceedsDebitsAccountAndDispenses() {
        Account a = new Account("CHK-1", 500);
        CashDispenser d = new CashDispenser(2000, true);
        TxnResult r = new Withdrawal(a, d, policy, 100, 0).execute();
        assertEquals(TxnOutcome.SUCCESS, r.getOutcome());
        assertEquals(400, a.getBalance());
        assertEquals(1900, d.getCashOnHand());
    }

    // ---------- UC8: invalid multiple ----------
    @Test
    void withdrawalRejectsNonMultiple() {
        Account a = new Account("CHK-1", 500);
        TxnResult r = new Withdrawal(a, new CashDispenser(2000, true), policy, 25, 0).execute();
        assertEquals(TxnOutcome.INVALID_MULTIPLE, r.getOutcome());
        assertEquals(500, a.getBalance());
    }

    // ---------- UC9: insufficient funds ----------
    @Test
    void withdrawalRejectsWhenInsufficientFunds() {
        Account a = new Account("CHK-1", 50);
        TxnResult r = new Withdrawal(a, new CashDispenser(2000, true), policy, 100, 0).execute();
        assertEquals(TxnOutcome.INSUFFICIENT_FUNDS, r.getOutcome());
        assertEquals(50, a.getBalance());
    }

    // ---------- UC10: daily limit ----------
    @Test
    @DisplayName("Withdrawal over the daily limit is rejected before touching funds")
    void withdrawalRejectsWhenOverDailyLimit() {
        Account a = new Account("CHK-1", 2000);                 // plenty of funds
        TxnResult r = new Withdrawal(a, new CashDispenser(5000, true), policy, 600, 0).execute();
        assertEquals(TxnOutcome.DAILY_LIMIT_EXCEEDED, r.getOutcome());
        assertEquals(2000, a.getBalance());
    }

    @Test
    void withdrawalRejectsWhenCumulativeExceedsDailyLimit() {
        Account a = new Account("CHK-1", 2000);
        // already withdrew 450; another 100 -> 550 > 500
        TxnResult r = new Withdrawal(a, new CashDispenser(5000, true), policy, 100, 450).execute();
        assertEquals(TxnOutcome.DAILY_LIMIT_EXCEEDED, r.getOutcome());
    }

    // ---------- dispenser unavailable branches ----------
    @Test
    void withdrawalRejectedWhenChuteNotOperational() {
        Account a = new Account("CHK-1", 500);
        TxnResult r = new Withdrawal(a, new CashDispenser(2000, false), policy, 100, 0).execute();
        assertEquals(TxnOutcome.DISPENSER_UNAVAILABLE, r.getOutcome());
        assertEquals(500, a.getBalance());
    }

    @Test
    void withdrawalRejectedWhenMachineLowOnCash() {
        Account a = new Account("CHK-1", 500);                  // funds OK
        CashDispenser d = new CashDispenser(40, true);          // but machine nearly empty
        TxnResult r = new Withdrawal(a, d, policy, 100, 0).execute();
        assertEquals(TxnOutcome.DISPENSER_UNAVAILABLE, r.getOutcome());
        assertEquals(500, a.getBalance());
        assertEquals(40, d.getCashOnHand());
    }

    // ---------- TxnResult ----------
    @Test
    void txnResultIsSuccessOnlyForSuccessOutcome() {
        assertTrue(new TxnResult(TxnOutcome.SUCCESS, 0).isSuccess());
        assertFalse(new TxnResult(TxnOutcome.INSUFFICIENT_FUNDS, 0).isSuccess());
    }
}
