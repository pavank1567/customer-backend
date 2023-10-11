package com.stalwart.customer.model;

import lombok.Data;

@Data
public class OTPObject extends Customer {

    String email;
    String otp;
    long insertTs;
    long expTs;
    public OTPObject(String email, String otp, long insertTs, long expTs) {
        this.email=email;
        this.otp = otp;
        this.insertTs=insertTs;
        this.expTs = expTs;
    }
}
