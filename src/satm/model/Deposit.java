package satm.model;

public class Deposit extends Transaction {

    private final DepositSlot slot;
    private final int amount;

    public Deposit(Account account, DepositSlot slot, int amount) {
        super(account);
        this.slot = slot;
        this.amount = amount;
    }

    @Override
    public TxnResult execute() {
        if (!slot.isAvailable()) {
            return new TxnResult(TxnOutcome.DEPOSIT_SLOT_UNAVAILABLE, account.getBalance());
        }
        slot.accept();
        account.deposit(amount);
        return new TxnResult(TxnOutcome.SUCCESS, account.getBalance());
    }
}
