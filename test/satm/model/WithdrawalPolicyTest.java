package satm.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class WithdrawalPolicyTest {

    private final WithdrawalPolicy policy = new WithdrawalPolicy();

    @Test
    void acceptsPositiveMultipleOfDenomination() {
        assertTrue(policy.isValidMultiple(10));
        assertTrue(policy.isValidMultiple(200));
    }

    @Test
    void rejectsNonMultiple() {           // UC8
        assertFalse(policy.isValidMultiple(25));
    }

    @Test
    void rejectsZeroAndNegativeAmounts() {
        assertFalse(policy.isValidMultiple(0));
        assertFalse(policy.isValidMultiple(-10));
    }

    @Test
    void withinDailyLimitBoundaries() {   // UC10
        // DAILY_LIMIT == 500
        assertTrue(policy.withinDailyLimit(200, 0));     // well under
        assertTrue(policy.withinDailyLimit(500, 0));     // exactly at limit
        assertTrue(policy.withinDailyLimit(100, 400));   // cumulative exactly at limit
        assertFalse(policy.withinDailyLimit(101, 400));  // cumulative just over
        assertFalse(policy.withinDailyLimit(600, 0));    // single request over
    }
}
