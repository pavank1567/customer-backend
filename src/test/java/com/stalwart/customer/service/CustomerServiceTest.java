package com.stalwart.customer.service;

import com.stalwart.customer.dao.CustomerDao;
import com.stalwart.customer.exceptions.UserAlreadyExistsException;
import com.stalwart.customer.exceptions.UserNotFoundException;
import com.stalwart.customer.exceptions.UserNotValidException;
import com.stalwart.customer.mapper.CustomerDTOMapper;
import com.stalwart.customer.model.Customer;
import com.stalwart.customer.model.CustomerDTO;
import com.stalwart.customer.model.CustomerRegistrationRequest;
import com.stalwart.customer.model.Gender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {


    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;
    @Mock
    private PasswordEncoder passwordEncoder;
    private CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();
    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao, passwordEncoder, customerDTOMapper);
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();
        verify(customerDao).getAllCustomers();
    }

    @Test
    void addCustomer() {
        String email = "email.com";

        when(customerDao.existsWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest customer = new CustomerRegistrationRequest(
                "name",
                email,
                22,
                "password",
                Gender.MALE
        );

        String passwordHash = "@#Random#@";
        when(passwordEncoder.encode(customer.getPassword())).thenReturn(passwordHash);
        underTest.addCustomer(customer);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).addCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNotNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());

    }

    @Test
    void willThrowEmailExists() {
        String email = "email.com";

        when(customerDao.existsWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest customer = new CustomerRegistrationRequest(
                "name",
                email,
                22,
                "password",
                Gender.MALE
        );

        assertThatThrownBy(()->underTest.addCustomer(customer))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Given email %s already exists".formatted(email));
        verify(customerDao, never()).addCustomer(any());
    }

    @Test
    void willThrowUNVEForName(){
        CustomerRegistrationRequest customer = new CustomerRegistrationRequest();
        customer.setEmail("email");
        customer.setAge(22);

        assertThatThrownBy(() -> underTest.addCustomer(customer))
                .isInstanceOf(UserNotValidException.class)
                .hasMessage("Name should not be null");
    }
    @Test
    void willThrowUNVEForEmail(){
        CustomerRegistrationRequest customer = new CustomerRegistrationRequest();
        customer.setName("name");
        customer.setAge(22);
        assertThatThrownBy(() -> underTest.addCustomer(customer))
                .isInstanceOf(UserNotValidException.class)
                .hasMessage("Email Should not be null");

    }
    @Test
    void willThrowUNVEForAge(){
        CustomerRegistrationRequest customer = new CustomerRegistrationRequest();
        customer.setName("name");
        customer.setEmail("email");

        assertThatThrownBy(() -> underTest.addCustomer(customer))
                .isInstanceOf(UserNotValidException.class)
                .hasMessage("Invalid Age");

    }

    @Test
    void getCustomerById() {

        int id = 1;

        Customer customer = new Customer(
                1,
                "name",
                "emil",
                "password",
                22,
                Gender.MALE);

        Mockito.when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);
        CustomerDTO actual = underTest.getCustomerById(id);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowExceptionForGetCustomerById() {

        int id = 1;

        Mockito.when(customerDao.getCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(()->underTest.getCustomerById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("user not found with id %s".formatted(id));
    }

    @Test
    void deleteCustomerById() {
        int id = 1;

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        underTest.deleteCustomerById(id);
        verify(customerDao).deleteCustomerById(id);

    }

    @Test
    void willThrowWhenUserNotExists(){

        int id = 1;
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        assertThatThrownBy(()->underTest.deleteCustomerById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("user not found with id %s".formatted(id));
        verify(customerDao, never()).deleteCustomerById(id);

    }

    @Test
    void getCustomerByEmail() {
        String email = "email";

        Customer customer = new Customer(
                1,
                "name",
                email,
                "password",
                22,
                Gender.MALE);

        Mockito.when(customerDao.getCustomerByEmail(email)).thenReturn(Optional.of(customer));

        CustomerDTO actual = underTest.getCustomerByEmail(email);
        CustomerDTO expected = customerDTOMapper.apply(customer);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowUNFForEmail(){
        String email = "email";

        Mockito.when(customerDao.getCustomerByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(()->underTest.getCustomerByEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("user not found with email %s".formatted(email));
    }

    @Test
    void deleteCustomerWithEmail() {
        String email = "email";

        when(customerDao.existsWithEmail(email)).thenReturn(true);

        underTest.deleteCustomerWithEmail(email);
        verify(customerDao).deleteCustomerByEmail(email);
    }

    @Test
    void willThrowUNFDeleteCustomerWithEmail() {
        String email = "email";

        when(customerDao.existsWithEmail(email)).thenReturn(false);

        assertThatThrownBy(()->underTest.deleteCustomerWithEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("user not found with email %s".formatted(email));
    }

    @Test
    void canUpdateAllCustomerProps() {
        int id = 1;

        Customer customer = new Customer(
                id,
                "name",
                "emil",
                "password",
                22,
                Gender.MALE);

        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        Customer updatedCustomer = new Customer(
                id,
                "updated_name",
                "updated_email",
                "password",
                100,
                Gender.MALE);
        when(customerDao.existsWithEmail(updatedCustomer.getEmail())).thenReturn(false);

        underTest.updateCustomer(updatedCustomer);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updatedCustomer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updatedCustomer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(updatedCustomer.getAge());
        assertThat(capturedCustomer.getId()).isEqualTo(updatedCustomer.getId());


    }

    @Test
    void canUpdateCustomerAge() {
        int id = 1;

        Customer customer = new Customer(
                id,
                "name",
                "emil",
                "password",
                22,
                Gender.MALE);

        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setAge(25);

        underTest.updateCustomer(updatedCustomer);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isNull();
        assertThat(capturedCustomer.getEmail()).isNull();
        assertThat(capturedCustomer.getAge()).isEqualTo(updatedCustomer.getAge());
        assertThat(capturedCustomer.getId()).isEqualTo(updatedCustomer.getId());


    }

    @Test
    void canUpdateCustomerEmail() {
        int id = 1;

        Customer customer = new Customer(
                id,
                "name",
                "emil",
                "password",
                22,
                Gender.MALE);

        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setEmail("new_email");

        when(customerDao.existsWithEmail(updatedCustomer.getEmail())).thenReturn(false);

        underTest.updateCustomer(updatedCustomer);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isNull();
        assertThat(capturedCustomer.getEmail()).isEqualTo(updatedCustomer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(0);
        assertThat(capturedCustomer.getId()).isEqualTo(updatedCustomer.getId());


    }

    @Test
    void canUpdateCustomerName() {
        int id = 1;

        Customer customer = new Customer(
                id,
                "name",
                "emil",
                "password",
                22,
                Gender.MALE);

        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setName("new_name");

        underTest.updateCustomer(updatedCustomer);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updatedCustomer.getName());
        assertThat(capturedCustomer.getEmail()).isNull();
        assertThat(capturedCustomer.getAge()).isEqualTo(0);
        assertThat(capturedCustomer.getId()).isEqualTo(updatedCustomer.getId());


    }

    @Test
    void willThrowUAEForUpdate(){
        int id = 1;

        Customer customer = new Customer(
                id,
                "name",
                "emil",
                "password",
                22,
                Gender.MALE);

        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setEmail("email");

        when(customerDao.existsWithEmail(updatedCustomer.getEmail())).thenReturn(true);

        assertThatThrownBy(()-> underTest.updateCustomer(updatedCustomer))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Given email %s already exists".formatted(updatedCustomer.getEmail()));
    }
}