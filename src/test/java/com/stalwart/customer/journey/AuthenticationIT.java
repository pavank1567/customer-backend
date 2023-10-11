package com.stalwart.customer.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.stalwart.customer.jwt.JWTUtil;
import com.stalwart.customer.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIT {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;
    private static final Faker faker = new Faker();

    private static final Random random = new Random();

    private static final String authAPI = "/api/auth";
    private static final String customerURI = "/api/customer";

    @Test
    void canLogin() {
        Name fakeName = faker.name();
        String firstName = fakeName.firstName();
        String lastName = fakeName.lastName();
        String name = firstName + " " + lastName ;
        String email = firstName+lastName+ String.valueOf(random.nextInt(1234,9999))+"@stalwart.com";
        int age = random.nextInt(18,100);

        //create a registration request
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name,
                email,
                age,
                "password",
                Gender.MALE
        );

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                email,
                "password"
        );

        //login without registering
        webTestClient.post()
                .uri(authAPI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();


        //send a post request (register customer)
       webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        EntityExchangeResult<Object> result =
                webTestClient.post()
                .uri(authAPI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                        .expectBody(new ParameterizedTypeReference<Object>() {})
                .returnResult();

        assertThat(
                jwtUtil.isTokenValid(
                        result.getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0),
                        email
                )
        );

    }
}
