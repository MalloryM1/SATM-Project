package satm.view;

import satm.model.TransactionType;

public interface ATMView {

    /** Render one of the 15 screens. */
    void display(Screen screen);

    /** Read the PAN on the inserted card (blank/"quit" = shut down). */
    String readCard();

    /** Read the customer's typed PIN. */
    String readPin();

    /** Read the transaction selection from screen 5. */
    TransactionType readTransactionChoice();

    /** Read a whole-dollar amount, re-prompts until input is valid. */
    int readAmount();

    /** Read a yes/no answer */
    boolean readYesNo(String prompt);

    /** Show the account balance */
    void showBalance(int balanceDollars);

    /** dispense the cash to the customer */
    void dispenseCash(int amount);

    /** Print the transaction receipt */
    void printReceipt(int balanceDollars);

    /** Return the card to the customer*/
    void ejectCard();

    /** Retain the card */
    void retainCard();

    /** Display an ad-hoc message not tied to one of the 15 screens. */
    void showMessage(String message);
}
