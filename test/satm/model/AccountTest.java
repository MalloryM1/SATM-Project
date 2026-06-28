package satm.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Unit tests for Account */
class AccountTest {

    @Test
    void opensWithGivenBalance() {
        Account a = new Account("CHK-1", 500);
        assertEquals(500, a.getBalance());
        assertEquals("CHK-1", a.getNumber());
    }

    @Test
    void negativeOpeningBalanceRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Account("CHK-1", -1));
    }

    @Test
    @DisplayName("hasFunds: greater, equal, and less than balance")
    void hasFundsBoundaries() {
        Account a = new Account("CHK-1", 100);
        assertTrue(a.hasFunds(50));    // balance > amount
        assertTrue(a.hasFunds(100));   // balance == amount (boundary)
        assertFalse(a.hasFunds(101));  // balance < amount
    }

    @Test
    void depositIncreasesBalance() {
        Account a = new Account("CHK-1", 100);
        a.deposit(250);
        assertEquals(350, a.getBalance());
    }

    @Test
    void depositMustBePositive() {
        Account a = new Account("CHK-1", 100);
        assertThrows(IllegalArgumentException.class, () -> a.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> a.deposit(-5));
    }

    @Test
    void withdrawDecreasesBalance() {
        Account a = new Account("CHK-1", 100);
        a.withdraw(40);
        assertEquals(60, a.getBalance());
    }

    @Test
    void withdrawMustBePositive() {
        Account a = new Account("CHK-1", 100);
        assertThrows(IllegalArgumentException.class, () -> a.withdraw(0));
        assertThrows(IllegalArgumentException.class, () -> a.withdraw(-10));
    }

    @Test
    void withdrawCannotOverdraw() {
        Account a = new Account("CHK-1", 30);
        assertThrows(IllegalStateException.class, () -> a.withdraw(40));
        assertEquals(30, a.getBalance(), "balance unchanged after failed withdrawal");
    }
}
