package satm.model;

public abstract class Transaction {

    protected final Account account;

    protected Transaction(Account account) {
        this.account = account;
    }

    /** Perform the transaction and return its outcome. */
    public abstract TxnResult execute();
}
