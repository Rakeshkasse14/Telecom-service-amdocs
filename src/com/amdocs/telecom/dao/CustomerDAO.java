package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.Customer;
import java.util.List;

public interface CustomerDAO {
    Customer findById(int customerId);
    Customer findByCustomerNumber(String customerNumber);
    List<Customer> findAll();
    boolean save(Customer customer);
    boolean update(Customer customer);
}
