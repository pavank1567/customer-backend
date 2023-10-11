package com.stalwart.customer.service;

import com.stalwart.customer.dao.CustomerDao;
import com.stalwart.customer.mapper.CustomerRowMapper;
import com.stalwart.customer.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerDataAccessService implements CustomerDao {

    public final JdbcTemplate jdbcTemplate;
    public final CustomerRowMapper customerRowMapper;

    public CustomerDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> getAllCustomers() {

        String selectQuey = "select * from customer;";

        return jdbcTemplate.query(selectQuey, customerRowMapper);

    }

    @Override
    public Optional<Customer> getCustomerById(int id) {
        String selectbyIdQuery = "select * from customer where id = ?;";
        return jdbcTemplate.query(selectbyIdQuery,customerRowMapper,id).stream().findFirst();
    }

    @Override
    public void deleteCustomerById(int id) {
        String deleteQuery = "delete from customer where id = ?;";
        jdbcTemplate.update(deleteQuery,id);
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        String selectbyEmailQuery = "select * from customer where email = ?;";
        return jdbcTemplate.query(selectbyEmailQuery,customerRowMapper,email).stream().findFirst();
    }

    @Override
    public void deleteCustomerByEmail(String email) {
        String deleteQuery = "delete from customer where email = ?;";
        jdbcTemplate.update(deleteQuery,email);
    }

    @Override
    public void addCustomer(Customer customer) {
        String addQuery = "insert into customer(name,email,age,gender,password) values(?,?,?,?,?)";
        jdbcTemplate.update(addQuery,customer.getName(), customer.getEmail(), customer.getAge(), customer.getGender().name(),customer.getPassword());
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        if(updatedCustomer.getName()!=null){
            String updateQuery = "update customer set name = ? where id = ?";
            jdbcTemplate.update(updateQuery,updatedCustomer.getName(),updatedCustomer.getId());
        }
        if(updatedCustomer.getEmail()!=null){
            String updateQuery = "update customer set email = ? where id = ?";
            jdbcTemplate.update(updateQuery,updatedCustomer.getEmail(),updatedCustomer.getId());
        }
        if(updatedCustomer.getAge()!=0){
            String updateQuery = "update customer set age = ? where id = ?";
            jdbcTemplate.update(updateQuery,updatedCustomer.getAge(),updatedCustomer.getId());
        }
    }

    @Override
    public boolean existsWithEmail(String email) {
        String query = "select count(*) from customer where email =?";
        int count = jdbcTemplate.queryForObject(query,Integer.class,email);
        if(count==0)
            return false;
        else
            return true;
    }

    @Override
    public boolean existsCustomerWithId(int id){
        String query = "select count(*) from customer where id =?";
        int count = jdbcTemplate.queryForObject(query,Integer.class,id);
        if(count==0)
            return false;
        else
            return true;
    }


}
