package satm.model;

public class Withdrawal extends Transaction {

    private final CashDispenser dispenser;
    private final WithdrawalPolicy policy;
    private final int amount;
    private final int alreadyWithdrawn;

    public Withdrawal(Account account, CashDispenser dispenser, WithdrawalPolicy policy,
                      int amount, int alreadyWithdrawn) {
        super(account);
        this.dispenser = dispenser;
        this.policy = policy;
        this.amount = amount;
        this.alreadyWithdrawn = alreadyWithdrawn;
    }

    @Override
    public TxnResult execute() {
        if (!dispenser.isOperational()) {                                  // chute jammed
            return new TxnResult(TxnOutcome.DISPENSER_UNAVAILABLE, account.getBalance());
        }
        if (!policy.isValidMultiple(amount)) {                             // SUC8
            return new TxnResult(TxnOutcome.INVALID_MULTIPLE, account.getBalance());
        }
        if (!policy.withinDailyLimit(amount, alreadyWithdrawn)) {          // SUC10
            return new TxnResult(TxnOutcome.DAILY_LIMIT_EXCEEDED, account.getBalance());
        }
        if (!account.hasFunds(amount)) {                                   // SUC9
            return new TxnResult(TxnOutcome.INSUFFICIENT_FUNDS, account.getBalance());
        }
        if (!dispenser.canDispense(amount)) {                              // machine low on cash
            return new TxnResult(TxnOutcome.DISPENSER_UNAVAILABLE, account.getBalance());
        }
        account.withdraw(amount);                                          // SUC7 success
        dispenser.dispense(amount);
        return new TxnResult(TxnOutcome.SUCCESS, account.getBalance());
    }

    public int getAmount() {
        return amount;
    }
}
