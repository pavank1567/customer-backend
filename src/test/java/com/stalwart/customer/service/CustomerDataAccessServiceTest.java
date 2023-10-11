package com.stalwart.customer.service;

import com.stalwart.customer.AbstractFirstTest;
import com.stalwart.customer.mapper.CustomerRowMapper;
import com.stalwart.customer.model.Customer;
import com.stalwart.customer.model.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerDataAccessServiceTest extends AbstractFirstTest {

    private CustomerDataAccessService customerDataAccessService;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();


    @BeforeEach
    void setUp(){
        customerDataAccessService = new CustomerDataAccessService(
                getJDBCTemplate(),
                customerRowMapper
        );
    }

    @Test
    void getAllCustomers() {
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().safeEmailAddress() + "." + UUID.randomUUID(),
                "password", 22, Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);
        List<Customer> expectedCustomers = customerDataAccessService.getAllCustomers();
        assertThat(expectedCustomers).isNotEmpty();
    }

    @Test
    void getCustomerById() {
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);

        int id = customerDataAccessService.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> selectedCustomer = customerDataAccessService.getCustomerById(id);

        assertThat(selectedCustomer).isPresent().hasValueSatisfying(c -> {
           assertThat(c.getId()).isEqualTo(id);
           assertThat(c.getName()).isEqualTo(customer.getName());
           assertThat(c.getEmail()).isEqualTo(customer.getEmail());
           assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void getCustomerWithWrongId(){
        int id = -1 ;
        Optional<Customer> customer = customerDataAccessService.getCustomerById(id);
        assertThat(customer).isEmpty();
    }

    @Test
    void deleteCustomerById() {
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);

        int id = customerDataAccessService.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        customerDataAccessService.deleteCustomerById(id);

        Optional<Customer> deletedCustomer = customerDataAccessService.getCustomerById(id);
        assertThat(deletedCustomer).isNotPresent();
    }

    @Test
    void getCustomerByEmail() {
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);

        int id = customerDataAccessService.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> selectedCustomer = customerDataAccessService.getCustomerByEmail(email);

        assertThat(selectedCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void deleteCustomerByEmail() {
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);
        customerDataAccessService.deleteCustomerByEmail(email);
        Optional<Customer> deletedCustomer = customerDataAccessService.getCustomerByEmail(email);
        assertThat(deletedCustomer).isNotPresent();
    }

    @Test
    void addCustomer() {
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);

        int id = customerDataAccessService.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> selectedCustomer = customerDataAccessService.getCustomerById(id);

        assertThat(selectedCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });

//        int numOfCustomers = customerDataAccessService.getAllCustomers().size();
//
//        assertThat(numOfCustomers).isEqualTo(1);
    }

    @Test
    void updateCustomer() {
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);

        int id = customerDataAccessService.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = "Magic";
        String newEmail = "random@email.com";
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);
        update.setEmail(newEmail);
        update.setAge(100);

        customerDataAccessService.updateCustomer(update);

        Optional<Customer> selectedCustomer = customerDataAccessService.getCustomerById(id);

        assertThat(selectedCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(100);
        });
    }

    @Test
    void updateCustomerName(){
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);

        int id = customerDataAccessService.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = "Magic";
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);

        customerDataAccessService.updateCustomer(update);

        Optional<Customer> selectedCustomer = customerDataAccessService.getCustomerById(id);

        assertThat(selectedCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail(){
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);

        int id = customerDataAccessService.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newEmail = "email@email.com";
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        customerDataAccessService.updateCustomer(update);

        Optional<Customer> selectedCustomer = customerDataAccessService.getCustomerById(id);

        assertThat(selectedCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge(){
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);

        int id = customerDataAccessService.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer update = new Customer();
        update.setId(id);
        update.setAge(100);

        customerDataAccessService.updateCustomer(update);

        Optional<Customer> selectedCustomer = customerDataAccessService.getCustomerById(id);

        assertThat(selectedCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(100);
        });
    }

    @Test
    void existsWithEmail() {

        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);

        boolean actual = customerDataAccessService.existsWithEmail(email);
        assertThat(actual).isTrue();

    }

    @Test
    void existsCustomerWithWrongEmail() {
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        boolean actual = customerDataAccessService.existsWithEmail(email);
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        String email = faker.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 22,Gender.MALE
                );

        customerDataAccessService.addCustomer(customer);

        int id = customerDataAccessService.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        boolean actual = customerDataAccessService.existsCustomerWithId(id);
        assertThat(actual).isTrue();

    }

    @Test
    void existsCustomerWithWrongId(){
        int id = -1;
        boolean actual = customerDataAccessService.existsCustomerWithId(id);
        assertThat(actual).isFalse();
    }



}