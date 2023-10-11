package com.stalwart.customer.model;

public record AuthenticationRequest(
        String username,
        String password
) {
}
