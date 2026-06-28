package satm.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/** Unit tests for Bank */
class BankTest {

    private Bank bankWith(Customer... customers) {
        Bank bank = new Bank();
        for (Customer c : customers) {
            bank.addCustomer(c);
        }
        return bank;
    }

    @Test
    void authenticateReturnsCustomerForKnownPan() {   // UC1
        Customer alice = new Customer("1001", "2468", "Alice", new Account("CHK-1", 100));
        Bank bank = bankWith(alice);
        assertSame(alice, bank.authenticate("1001"));
    }

    @Test
    void authenticateReturnsNullForUnknownPan() {     // UC2
        Bank bank = bankWith(new Customer("1001", "2468", "Alice", new Account("CHK-1", 100)));
        assertNull(bank.authenticate("9999"));
    }

    @Test
    void resolvesAmongMultipleCustomers() {
        Customer alice = new Customer("1001", "2468", "Alice", new Account("CHK-1", 100));
        Customer bob = new Customer("1002", "1357", "Bob", new Account("CHK-2", 75));
        Bank bank = bankWith(alice, bob);
        assertSame(bob, bank.authenticate("1002"));
    }
}
