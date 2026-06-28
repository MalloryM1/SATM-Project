package satm.model;

public class WithdrawalPolicy {

    /** Withdrawals must be a positive multiple of this denomination. */
    public static final int BILL_DENOMINATION = 10;

    /** Maximum total a customer can withdraw in a single day. */
    public static final int DAILY_LIMIT = 500;

    public boolean isValidMultiple(int amount) {
        return amount > 0 && amount % BILL_DENOMINATION == 0;
    }

    public boolean withinDailyLimit(int amount, int alreadyWithdrawn) {
        return alreadyWithdrawn + amount <= DAILY_LIMIT;
    }
}
