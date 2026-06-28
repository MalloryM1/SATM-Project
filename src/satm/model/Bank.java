package satm.model;

import java.util.HashMap;
import java.util.Map;

public class Bank {

    private final Map<String, Customer> customersByPan = new HashMap<>();

    public void addCustomer(Customer customer) {
        customersByPan.put(customer.getPan(), customer);
    }

    /**Looks up the customer whose card carries this PAN.*/
    public Customer authenticate(String pan) {
        return customersByPan.get(pan);
    }
}
