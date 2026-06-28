package satm.model;


public class Customer {

    private final String pan;   // Primary Account Number
    private final String pin;   // Personal Identification Number
    private final String name;
    private final Account account;

    public Customer(String pan, String pin, String name, Account account) {
        this.pan = pan;
        this.pin = pin;
        this.name = name;
        this.account = account;
    }

    public String getPan() {
        return pan;
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }

    /** return true if the given PIN matches this customer's PIN. */
    public boolean verifyPin(String candidatePin) {
        return pin.equals(candidatePin);
    }
}
