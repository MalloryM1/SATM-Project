package satm.model;

public enum TxnOutcome {
    SUCCESS,
    INSUFFICIENT_FUNDS,        // account balance too low          -> screen 8
    INVALID_MULTIPLE,          // amount not a multiple of $10      -> screen 9
    DAILY_LIMIT_EXCEEDED,      // over the per-session daily limit  -> re-prompt
    DISPENSER_UNAVAILABLE,     // chute jammed / not enough cash    -> screen 10
    DEPOSIT_SLOT_UNAVAILABLE   // deposit slot problem              -> screen 12
}
