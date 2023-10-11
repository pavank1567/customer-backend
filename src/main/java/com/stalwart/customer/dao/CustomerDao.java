package com.stalwart.customer.dao;

import com.stalwart.customer.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDao {

    public List<Customer> getAllCustomers();
    public Optional<Customer> getCustomerById(int id);
    public void deleteCustomerById(int id);
    public Optional<Customer> getCustomerByEmail(String email);
    public void deleteCustomerByEmail(String email);

    public void addCustomer(Customer customer);

    public void updateCustomer(Customer customer);

    public boolean existsWithEmail(String email);

    boolean existsCustomerWithId(int id);
}
