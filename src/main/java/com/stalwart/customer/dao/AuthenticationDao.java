package com.stalwart.customer.dao;

import com.stalwart.customer.model.OTPObject;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthenticationDao {
    public void storeOTP(String email, String otp);

    Optional<OTPObject> validateOTP(String email, String otp, long ts);
}
