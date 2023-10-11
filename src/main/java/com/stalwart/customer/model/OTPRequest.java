package com.stalwart.customer.model;

public record OTPRequest(
        String email,
        String otp
) {
}
