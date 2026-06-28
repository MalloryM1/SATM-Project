package satm.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CashDispenserTest {

    @Test
    void canDispenseWhenOperationalAndEnoughCash() {
        CashDispenser d = new CashDispenser(500, true);
        assertTrue(d.canDispense(500));   // boundary: exactly enough
        assertTrue(d.canDispense(100));
    }

    @Test
    void cannotDispenseWhenNotEnoughCash() {
        CashDispenser d = new CashDispenser(50, true);
        assertFalse(d.canDispense(100));
    }

    @Test
    void cannotDispenseWhenNotOperational() {
        CashDispenser d = new CashDispenser(500, false);
        assertFalse(d.canDispense(100));
        assertFalse(d.isOperational());
    }

    @Test
    void dispenseReducesCashOnHand() {
        CashDispenser d = new CashDispenser(500, true);
        d.dispense(120);
        assertEquals(380, d.getCashOnHand());
    }

    @Test
    void dispenseThrowsWhenItCannot() {
        CashDispenser d = new CashDispenser(50, true);
        assertThrows(IllegalStateException.class, () -> d.dispense(100));
        assertEquals(50, d.getCashOnHand(), "cash unchanged after failed dispense");
    }
}
