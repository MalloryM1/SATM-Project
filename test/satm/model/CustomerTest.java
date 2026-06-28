package satm.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CustomerTest {

    private final Account account = new Account("CHK-1", 100);
    private final Customer customer = new Customer("1001", "2468", "Alice", account);

    @Test
    void verifyPinAcceptsCorrectPin() {
        assertTrue(customer.verifyPin("2468"));
    }

    @Test
    void verifyPinRejectsWrongPin() {
        assertFalse(customer.verifyPin("0000"));
    }

    @Test
    void exposesIdentityAndAccount() {
        assertEquals("1001", customer.getPan());
        assertEquals("Alice", customer.getName());
        assertSame(account, customer.getAccount());
    }
}
