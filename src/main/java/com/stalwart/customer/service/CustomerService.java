package com.stalwart.customer.service;

import com.stalwart.customer.dao.CustomerDao;
import com.stalwart.customer.exceptions.UserAlreadyExistsException;
import com.stalwart.customer.exceptions.UserNotFoundException;
import com.stalwart.customer.exceptions.UserNotValidException;
import com.stalwart.customer.mapper.CustomerDTOMapper;
import com.stalwart.customer.model.Customer;
import com.stalwart.customer.model.CustomerDTO;
import com.stalwart.customer.model.CustomerRegistrationRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;

    private final CustomerDTOMapper customerDTOMapper;
//    private final CustomerRepository customerRepository;

    public CustomerService(CustomerDao customerDao,
                           PasswordEncoder passwordEncoder,
                           CustomerDTOMapper customerDTOMapper) {
        this.customerDao = customerDao;
//        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }

    public List<CustomerDTO> getAllCustomers(){

        return customerDao.getAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public void addCustomer(CustomerRegistrationRequest request){

        if(request.getName()==null)
            throw new UserNotValidException("Name should not be null");
        if(request.getEmail()==null)
            throw new UserNotValidException("Email Should not be null");
        if(request.getAge()<=0)
            throw new UserNotValidException("Invalid Age");

        if(customerDao.existsWithEmail(request.getEmail())){
            throw new UserAlreadyExistsException("Given email %s already exists".formatted(request.getEmail()));
        }

        Customer customer = new Customer(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getAge(),
                request.getGender());
//        customerRepository.save(customer);
        customerDao.addCustomer(customer);
    }

    public CustomerDTO getCustomerById(int id) {
        return customerDao.getCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow( () ->
                new UserNotFoundException("user not found with id %s".formatted(id))
                );
    }

    public void deleteCustomerById(int id){
        boolean isCustomerPresent = customerDao.existsCustomerWithId(id);
        if(isCustomerPresent)
            customerDao.deleteCustomerById(id);
        else
            throw new UserNotFoundException("user not found with id %s".formatted(id));

    }

    public CustomerDTO getCustomerByEmail(String email){
        return customerDao.getCustomerByEmail(email)
                .map(customerDTOMapper)
                .orElseThrow(()->
                new UserNotFoundException("user not found with email %s".formatted(email))
                );
    }
    public void deleteCustomerWithEmail(String email){
        boolean isCustomerExists = customerDao.existsWithEmail(email);
        if(isCustomerExists)
            customerDao.deleteCustomerByEmail(email);
        else
            throw new UserNotFoundException("user not found with email %s".formatted(email));
    }

    public void updateCustomer(Customer updatedCustomer){
        Customer customer = customerDao.getCustomerById(updatedCustomer.getId())
                .orElseThrow(()->
                        new UserNotFoundException("user not found with id %s".formatted(updatedCustomer.getId()))
                );
        boolean changes = false;
        if(updatedCustomer.getName()!=null && !customer.getName().equalsIgnoreCase(updatedCustomer.getName())){
            changes =true;
        }
        else
            updatedCustomer.setName(null);

        if(updatedCustomer.getEmail()!=null && !customer.getEmail().equalsIgnoreCase(updatedCustomer.getEmail())){
            if(customerDao.existsWithEmail(updatedCustomer.getEmail())){
                throw new UserAlreadyExistsException("Given email %s already exists".formatted(updatedCustomer.getEmail()));
            }
            changes = true;
        }
        else
            updatedCustomer.setEmail(null);

        if(updatedCustomer.getAge()!=0 && customer.getAge()!=updatedCustomer.getAge()){
            changes = true;
        }
        else
            updatedCustomer.setAge(0);

        if(changes)
            customerDao.updateCustomer(updatedCustomer);
        else
            ;
    }

}
