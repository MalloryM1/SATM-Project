package satm.model;

public final class TxnResult {

    private final TxnOutcome outcome;
    private final int balance;

    public TxnResult(TxnOutcome outcome, int balance) {
        this.outcome = outcome;
        this.balance = balance;
    }

    public TxnOutcome getOutcome() {
        return outcome;
    }

    public int getBalance() {
        return balance;
    }

    public boolean isSuccess() {
        return outcome == TxnOutcome.SUCCESS;
    }
}
