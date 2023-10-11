package com.stalwart.customer.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OTPService {

    public static Map<String, Map<String,Long>> otpMap = new HashMap<>();

    public void storeOtp(String email, String otp) {
        Map<String, Long> tsMap = new HashMap<>();
        tsMap.put(otp,System.currentTimeMillis() + 60000);
        otpMap.put(email,tsMap);
    }

    public boolean validateOtp(String email, String otp, Long ts){
        return otpMap.get(email) != null &&
                otpMap.get(email).get(otp) != null &&
                otpMap.get(email).get(otp) > ts;
    }
}
