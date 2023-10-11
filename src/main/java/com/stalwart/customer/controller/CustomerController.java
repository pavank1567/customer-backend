package com.stalwart.customer.controller;
import com.stalwart.customer.jwt.JWTUtil;
import com.stalwart.customer.model.Customer;
import com.stalwart.customer.model.CustomerDTO;
import com.stalwart.customer.model.CustomerRegistrationRequest;
import com.stalwart.customer.service.CustomerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/all")
    public List<CustomerDTO> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody CustomerRegistrationRequest customer){
        customerService.addCustomer(customer);
        String jwtToken = jwtUtil.issueToken(customer.getEmail(),"ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,jwtToken)
                .build();
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomerById(@PathVariable int id){
        return customerService.getCustomerById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomerById(@PathVariable int id){
        customerService.deleteCustomerById(id);
    }

    @GetMapping("/email/{email}")
    public CustomerDTO getCustomerByEmail(@PathVariable String email){
        return customerService.getCustomerByEmail(email);
    }

    @DeleteMapping("/email/{email}")
    public void deleteCustomerByemail(@PathVariable String email){
        customerService.deleteCustomerWithEmail(email);
    }

    @PutMapping("/{id}")
    public void updateCustomer(@PathVariable int id, @RequestBody CustomerRegistrationRequest request){

        Customer customer = new Customer(request);
        customer.setId(id);
        customerService.updateCustomer(customer);
    }

}
