package satm.view;

import satm.model.TransactionType;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleATMView implements ATMView {

    private final Scanner in;
    private final PrintStream out;

    public ConsoleATMView() {
        this(new Scanner(System.in), System.out);
    }

    public ConsoleATMView(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void display(Screen screen) {
        out.println();
        out.println("+---------------------------------------------+");
        for (String line : screen.getText().split("\n")) {
            out.printf("| %-43s |%n", line);
        }
        out.println("+---------------------------------------------+");
    }

    @Override
    public String readCard() {
        return prompt("Insert card (enter PAN, or leave blank to shut down): ");
    }

    @Override
    public String readPin() {
        return prompt("Enter PIN: ");
    }

    @Override
    public TransactionType readTransactionChoice() {
        while (true) {
            String choice = prompt("Choose [b]alance / [d]eposit / [w]ithdrawal: ")
                    .trim().toLowerCase();
            switch (choice) {
                case "b", "balance"    -> { return TransactionType.BALANCE; }
                case "d", "deposit"    -> { return TransactionType.DEPOSIT; }
                case "w", "withdrawal" -> { return TransactionType.WITHDRAWAL; }
                default -> out.println("  Invalid selection. Please try again.");
            }
        }
    }

    @Override
    public int readAmount() {
        while (true) {
            String raw = prompt("Enter amount ($): ").trim();
            try {
                int amount = Integer.parseInt(raw);
                if (amount <= 0) {
                    out.println("  Amount must be positive. Please try again.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                out.println("  Please enter a whole-dollar number.");
            }
        }
    }

    @Override
    public boolean readYesNo(String message) {
        while (true) {
            String answer = prompt(message + " [y/n]: ").trim().toLowerCase();
            switch (answer) {
                case "y", "yes" -> { return true; }
                case "n", "no"  -> { return false; }
                default -> out.println("  Please answer y or n.");
            }
        }
    }

    @Override
    public void showBalance(int balanceDollars) {
        out.printf("    >> %s%n", formatMoney(balanceDollars));
    }

    @Override
    public void dispenseCash(int amount) {
        out.printf("    >> Dispensing %s in cash...%n", formatMoney(amount));
    }

    @Override
    public void printReceipt(int balanceDollars) {
        out.println("    >> Printing receipt. New balance: " + formatMoney(balanceDollars));
    }

    @Override
    public void ejectCard() {
        out.println("    >> Card returned. Please take your card.");
    }

    @Override
    public void retainCard() {
        out.println("    >> Card retained by machine.");
    }

    @Override
    public void showMessage(String message) {
        out.println(message);
    }

    private String formatMoney(int dollars) {
        return String.format("$%,d.00", dollars);
    }

    /**
     * Print a prompt and read one line. Throws {@link EndOfInputException} at
     * EOF so the controller can shut down cleanly from anywhere in the flow.
     */
    private String prompt(String label) {
        out.print(label);
        out.flush();
        try {
            return in.nextLine();
        } catch (NoSuchElementException e) {
            throw new EndOfInputException();
        }
    }
}
