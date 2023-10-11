package com.stalwart.customer.service;

import com.stalwart.customer.dao.AuthenticationDao;
import com.stalwart.customer.dao.CustomerDao;
import com.stalwart.customer.exceptions.UserNotFoundException;
import com.stalwart.customer.jwt.JWTUtil;
import com.stalwart.customer.mapper.CustomerDTOMapper;
import com.stalwart.customer.model.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomerDTOMapper customerDTOMapper;

    private final JWTUtil jwtUtil;

    private final AuthenticationDao authenticationDao;

    private final CustomerDao customerDao;

    private final OTPService otpService;

    public AuthService(AuthenticationManager authenticationManager, CustomerDTOMapper customerDTOMapper, JWTUtil jwtUtil, AuthenticationDao authenticationDao, CustomerDao customerDao, OTPService otpService) {
        this.authenticationManager = authenticationManager;
        this.customerDTOMapper = customerDTOMapper;
        this.jwtUtil = jwtUtil;
        this.authenticationDao = authenticationDao;
        this.customerDao = customerDao;
        this.otpService = otpService;
    }


    public AuthenticationResponse login(AuthenticationRequest request) {
       Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
       Customer principal = (Customer) authentication.getPrincipal();
       CustomerDTO customer = customerDTOMapper.apply(principal);
       String jwtToken = jwtUtil.issueToken(customer.getUsername(), customer.getRoles());
       return new AuthenticationResponse(jwtToken,customer);
    }

    public void storeOtp(String email, String otp) {

        otpService.storeOtp(email,otp);
        boolean isCustomerPresent = customerDao.existsWithEmail(email);

        if(isCustomerPresent)
            authenticationDao.storeOTP(email, otp);
        else
            throw new UserNotFoundException("user not found with email %s".formatted(email));
    }

    public boolean validateOTP(String email, String otp, long ts) {

        OTPObject otpObject = authenticationDao.validateOTP(email, otp, ts).get();

        if(otpService.validateOtp(email,otp,ts)) {
            return true;
        }

        if(otpObject.getEmail()!=null &&
                otpObject.getEmail().equals(email) &&
                otpObject.getOtp()!=null &&
                otpObject.getOtp().equals(otp) ) {

            return otpObject.getExpTs() >= ts;
        }
        return false;
    }
}
