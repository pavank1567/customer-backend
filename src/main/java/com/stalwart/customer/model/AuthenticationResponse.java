package com.stalwart.customer.model;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO
) {
}
