package satm.controller;

import satm.model.Customer;


public class Session {

    private Customer customer;
    private int pinAttempts;
    private int dailyWithdrawn;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /** Record a failed PIN attempt and return the new attempt count. */
    public int incrementPinAttempts() {
        return ++pinAttempts;
    }

    public void addWithdrawal(int amount) {
        dailyWithdrawn += amount;
    }

    public int getDailyWithdrawn() {
        return dailyWithdrawn;
    }
}
