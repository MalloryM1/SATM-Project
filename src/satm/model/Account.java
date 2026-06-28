package satm.model;

/**
 * A customer's checking account. A single checking account per customer.
 */
public class Account {

    private final String number;
    private int balance;

    public Account(String number, int openingBalance) {
        if (openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative");
        }
        this.number = number;
        this.balance = openingBalance;
    }

    public String getNumber() {
        return number;
    }

    public int getBalance() {
        return balance;
    }

    /** return true if the account has at least the requested amount of dollars available. */
    public boolean hasFunds(int amount) {
        return balance >= amount;
    }

    public void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
    }

    public void withdraw(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (!hasFunds(amount)) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance -= amount;
    }
}
