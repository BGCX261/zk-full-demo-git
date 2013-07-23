package org.zk.demo.managecustomers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerService {
    private static Map<String, Customer> customers = new HashMap<String, Customer>();
    static {
        customers.put("1", new Customer(1, "java", Date.valueOf("1999-02-01")));
        customers.put("2", new Customer(2, "groovy", Date.valueOf("2005-12-01")));
        customers.put("3", new Customer(3, "scala", Date.valueOf("2011-10-18")));
    }

    public int getCustomerCount() {
        return customers.size();
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<Customer>(customers.values());
    }

    public Customer getCustomer(String id) {
        return customers.get(id);
    }

    public void addCustomer(Customer customer) throws Exception {
        customers.put(String.valueOf(customer.getId()), customer);

    }

    public void updateCustomer(Customer customer) {
        customers.put(String.valueOf(customer.getId()), customer);
    }
}
