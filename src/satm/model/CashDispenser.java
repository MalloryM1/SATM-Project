package satm.model;

public class CashDispenser {

    private int cashOnHand;
    private boolean operational;

    public CashDispenser(int cashOnHand, boolean operational) {
        this.cashOnHand = cashOnHand;
        this.operational = operational;
    }

    public boolean isOperational() {
        return operational;
    }

    /** return true if the chute is working and holds enough money. */
    public boolean canDispense(int amount) {
        return operational && amount <= cashOnHand;
    }

    public void dispense(int amount) {
        if (!canDispense(amount)) {
            throw new IllegalStateException("Cannot dispense requested amount");
        }
        cashOnHand -= amount;
    }

    public int getCashOnHand() {
        return cashOnHand;
    }
}
