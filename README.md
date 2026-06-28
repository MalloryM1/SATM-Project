# SATM Simulator — Part (b) Implementation

A console implementation of Jorgensen's Simple ATM (SATM) system.

## How to run

**In IntelliJ:** open the project, ensure the Project SDK is set to a JDK 17
(Corretto 17 is installed), then run `satm.Main`.

At the welcome screen, insert a card by typing a PAN. Enter a **blank line**
(or `quit`) at the welcome screen to shut the machine down.

### Sample customers

| PAN  | PIN  | Name         | Opening balance |
|------|------|--------------|-----------------|
| 1001 | 2468 | Alice Walker | $500            |
| 1002 | 1357 | Bob Stone    | $75             |

#
- **Model** knows banking facts only. It returns domain `TxnOutcome`s
  (`INSUFFICIENT_FUNDS`, `INVALID_MULTIPLE`, …)
- **View** renders the 15 screens and parses/validates input. Implements an interface
- **Controller** is the state machine: read input → call model → map outcome to
  the next screen. The only component aware of the screen flow.

## Use-case coverage (Appendix A.2 / Table 14.7)

| Use case | Handled by | Screens |
|----------|------------|---------|
| SUC1 Valid card swipe        | `ATMController.cardEntry` / `Bank.authenticate` | 1 → 2 |
| SUC2 Invalid card swipe      | `cardEntry`                                     | 1 → 4 (retain) |
| SUC3 Correct PIN attempt     | `pinEntry` / `Customer.verifyPin`               | 2 → 5 |
| SUC4 Failed PIN attempt      | `pinEntry` (3 tries → retain)                   | 2 → 3 … → 4 |
| SUC5 Choose Balance          | `doBalance` / `BalanceInquiry`                  | 5 → 6 → 14 |
| SUC6 Choose Deposit          | `doDeposit` / `Deposit`, `DepositSlot`          | 5 → 7 → 13 → 14 (or 12) |
| SUC7 Withdrawal valid        | `doWithdrawal` / `Withdrawal`                   | 5 → 7 → 11 → 14 |
| SUC8 Withdrawal wrong mult.  | `WithdrawalPolicy.isValidMultiple`              | 7 → 9 → 7 |
| SUC9 Withdrawal > balance    | `Account.hasFunds`                              | 7 → 8 → 7 |
| SUC10 Withdrawal > daily lim | `WithdrawalPolicy.withinDailyLimit`, `Session`  | 7 → message → 7 |
| SUC11 No other transaction   | `transactionLoop` (answer "no")                 | 14/12/10 → 15 → 1 |
| SUC12 Another transaction    | `transactionLoop` (answer "yes")                | 14/12/10 → 5 |

## Spec-interpretation decisions

1. **Withdrawal multiple ($10 vs $20).** Screen 7 says "multiples of $10" but
   SUC8 says "$20". The code follows the on-screen text ($10) via the single
   constant `WithdrawalPolicy.BILL_DENOMINATION`. Change that one value to
   switch to whatever Appendix A.2 mandates.
2. **Daily limit (SUC10).** The 15-screen spec defines no daily-limit screen, so
   this outcome is surfaced as a re-prompt message rather than a numbered screen.
   Limit = `WithdrawalPolicy.DAILY_LIMIT` ($500).
3. **Screen 9** is used for the "amount not a valid $10 multiple" case (its text
   is "Machine can only dispense $10 notes"); **screen 8** for insufficient
   account funds; **screen 10** for a jammed chute / machine out of cash.
4. **Money** is stored as whole-dollar `int` values to avoid floating-point
   rounding; it is only formatted as currency for display.
5. **Shutdown.** A blank card entry or end-of-input ends the simulation cleanly
   (handy for scripted/piped runs).
