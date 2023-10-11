package com.stalwart.customer.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.stalwart.customer.mapper.CustomerDTOMapper;
import com.stalwart.customer.model.Customer;
import com.stalwart.customer.model.CustomerDTO;
import com.stalwart.customer.model.CustomerRegistrationRequest;
import com.stalwart.customer.model.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    private static final Faker faker = new Faker();

    private static final Random random = new Random();

    private static final String customerURI = "/api/customer";

    @Test
    void canRegisterCustomer() {
        Name fakeName = faker.name();
        String firstName = fakeName.firstName();
        String lastName = fakeName.lastName();
        String name = firstName + " " + lastName ;
        String email = firstName+lastName+ String.valueOf(random.nextInt(1234,9999))+"@stalwart.com";
        int age = random.nextInt(18,100);

        //create a registration request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age,
                "password",
                Gender.MALE
        );

        //send a post request
        String token = webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        //getAllcustomers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(customerURI+"/all")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer "+ token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        //make sure customer is inserted

        CustomerDTO expectedCustomer = new CustomerDTO(
                null,
                name,
                email,
                Gender.MALE,
                age,
                List.of("ROLE_USER"),
                email
        );

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        //get customer with id
        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(expectedCustomer.getEmail()))
                .map(CustomerDTO::getId)
                .findFirst()
                .orElseThrow();
        expectedCustomer.setId(id);

        webTestClient.get()
                .uri(customerURI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer "+ token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        Name fakeName = faker.name();
        String firstName = fakeName.firstName();
        String lastName = fakeName.lastName();
        String name = firstName + " " + lastName ;
        String email = firstName+lastName+ String.valueOf(random.nextInt(1234,9999))+"@stalwart.com";
        int age = random.nextInt(18,100);

        //create a registration request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age,
                "password",
                Gender.MALE
        );

        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name,
                email +"stalwart.com",
                age,
                "password",
                Gender.MALE
        );

        //send a post request to add customer 1
        webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get token to delete customer 1
        String token = webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        //getAllcustomers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(customerURI+"/all")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer "+ token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(CustomerDTO::getId)
                .findFirst()
                .orElseThrow();

        //delete customer
        webTestClient.delete()
                .uri(customerURI+"/{id}",id)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer "+ token))
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        webTestClient.get()
                .uri(customerURI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer "+ token))
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void canUpdateCustomer() {
        Name fakeName = faker.name();
        String firstName = fakeName.firstName();
        String lastName = fakeName.lastName();
        String name = firstName + " " + lastName ;
        String email = firstName+lastName+ String.valueOf(random.nextInt(1234,9999))+"@stalwart.com";
        int age = random.nextInt(18,100);

        //create a registration request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age,
                "password",
                Gender.MALE
        );

        //send a post request
        String token = webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);


        //getAllcustomers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(customerURI+"/all")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer "+ token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(request.getEmail()))
                .map(CustomerDTO::getId)
                .findFirst()
                .orElseThrow();


        fakeName = faker.name();
        firstName = fakeName.firstName();
        lastName = fakeName.lastName();
        name = firstName + " " + lastName ;
        email = firstName+lastName+ String.valueOf(random.nextInt(1234,9999))+"@stalwart.com";
        age = random.nextInt(18,100);

        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest();
        updateRequest.setName(name);
        //delete customer
        webTestClient.put()
                .uri(customerURI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer "+ token))
                .body(Mono.just(updateRequest),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        CustomerDTO updatedCustomer = webTestClient.get()
                .uri(customerURI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer "+ token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        CustomerDTO expected = new CustomerDTO(
                null,
                request.getName(),
                request.getEmail(),
                request.getGender(),
                request.getAge(),
                List.of("ROLE_USER"),
                request.getEmail()
        );
        expected.setId(id);
        expected.setName(name);

        assertThat(expected).isEqualTo(updatedCustomer);
    }
}
