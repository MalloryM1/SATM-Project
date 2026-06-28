package satm.model;

public class DepositSlot {

    private boolean available;

    public DepositSlot(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    /** Accept the deposit envelope that the customer inserted. */
    public void accept() {
        if (!available) {
            throw new IllegalStateException("Deposit slot is not available");
        }
    }
}
