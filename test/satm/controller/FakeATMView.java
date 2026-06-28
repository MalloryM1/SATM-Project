package satm.controller;

import satm.model.TransactionType;
import satm.view.ATMView;
import satm.view.EndOfInputException;
import satm.view.Screen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


class FakeATMView implements ATMView {

    private final Deque<String> cards = new ArrayDeque<>();
    private final Deque<String> pins = new ArrayDeque<>();
    private final Deque<Integer> amounts = new ArrayDeque<>();
    private final Deque<TransactionType> choices = new ArrayDeque<>();
    private final Deque<Boolean> yesNo = new ArrayDeque<>();

    // ---- recorded output ----
    final List<Screen> displayed = new ArrayList<>();
    final List<Integer> balancesShown = new ArrayList<>();
    final List<Integer> cashDispensed = new ArrayList<>();
    final List<Integer> receipts = new ArrayList<>();
    final List<String> messages = new ArrayList<>();
    int ejectCount = 0;
    int retainCount = 0;

    // ---- fluent input scripting ----
    FakeATMView cards(String... v)          { for (String s : v) cards.add(s); return this; }
    FakeATMView pins(String... v)           { for (String s : v) pins.add(s); return this; }
    FakeATMView amounts(int... v)           { for (int s : v) amounts.add(s); return this; }
    FakeATMView choices(TransactionType... v) { for (TransactionType s : v) choices.add(s); return this; }
    FakeATMView yesNo(boolean... v)         { for (boolean s : v) yesNo.add(s); return this; }

    // ---- ATMView: input ----
    @Override public String readCard()  { return next(cards); }
    @Override public String readPin()   { return next(pins); }
    @Override public int readAmount()   { return next(amounts); }
    @Override public TransactionType readTransactionChoice() { return next(choices); }
    @Override public boolean readYesNo(String prompt)        { return next(yesNo); }

    // ---- ATMView: output (recorded) ----
    @Override public void display(Screen screen)     { displayed.add(screen); }
    @Override public void showBalance(int dollars)   { balancesShown.add(dollars); }
    @Override public void dispenseCash(int amount)   { cashDispensed.add(amount); }
    @Override public void printReceipt(int dollars)  { receipts.add(dollars); }
    @Override public void ejectCard()                { ejectCount++; }
    @Override public void retainCard()               { retainCount++; }
    @Override public void showMessage(String message) { messages.add(message); }

    // ---- assertion helpers ----
    boolean displayed(Screen screen) { return displayed.contains(screen); }
    long countOf(Screen screen)      { return displayed.stream().filter(s -> s == screen).count(); }

    private <T> T next(Deque<T> queue) {
        if (queue.isEmpty()) {
            throw new EndOfInputException();
        }
        return queue.poll();
    }
}
