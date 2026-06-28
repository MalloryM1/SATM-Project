package satm.view;


public enum Screen {
    SCREEN1_WELCOME(
            "Welcome to MM Federal Credit Union\nPlease insert your ATM card"),
    SCREEN2_ENTER_PIN(
            "Please enter your PIN\n____"),
    SCREEN3_PIN_INCORRECT(
            "Your PIN is incorrect.\nPlease try again."),
    SCREEN4_INVALID_CARD(
            "Invalid ATM card. It will be retained."),
    SCREEN5_SELECT_TXN(
            "Select transaction:\n  balance    >\n  deposit    >\n  withdrawal >"),
    SCREEN6_BALANCE(
            "Your balance is:"),
    SCREEN7_ENTER_AMOUNT(
            "Enter amount.\nWithdrawals must be multiples of $10"),
    SCREEN8_INSUFFICIENT_FUNDS(
            "Insufficient Funds!\nPlease enter a new amount"),
    SCREEN9_DENOMINATION(
            "Machine can only dispense $10 notes.\nPlease enter a new amount"),
    SCREEN10_NO_WITHDRAWAL(
            "Temporarily unable to process withdrawals.\nAnother transaction?"),
    SCREEN11_TAKE_CASH(
            "Your balance is being updated.\nPlease take cash from dispenser."),
    SCREEN12_NO_DEPOSIT(
            "Temporarily unable to process deposits.\nAnother transaction?"),
    SCREEN13_INSERT_DEPOSIT(
            "Please insert deposit into deposit slot."),
    SCREEN14_NEW_BALANCE(
            "Your new balance is being printed.\nAnother transaction?"),
    SCREEN15_TAKE_CARD(
            "Please take your receipt and ATM card.\nThank you.");

    private final String text;

    Screen(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
