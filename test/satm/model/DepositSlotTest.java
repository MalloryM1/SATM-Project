package satm.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DepositSlotTest {

    @Test
    void availableSlotAcceptsEnvelope() {
        DepositSlot slot = new DepositSlot(true);
        assertTrue(slot.isAvailable());
        assertDoesNotThrow(slot::accept);
    }

    @Test
    void unavailableSlotReportsAndRefuses() {
        DepositSlot slot = new DepositSlot(false);
        assertFalse(slot.isAvailable());
        assertThrows(IllegalStateException.class, slot::accept);
    }
}
