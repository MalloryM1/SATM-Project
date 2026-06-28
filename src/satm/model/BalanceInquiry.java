package satm.model;

public class BalanceInquiry extends Transaction {

    public BalanceInquiry(Account account) {
        super(account);
    }

    @Override
    public TxnResult execute() {
        return new TxnResult(TxnOutcome.SUCCESS, account.getBalance());
    }
}
