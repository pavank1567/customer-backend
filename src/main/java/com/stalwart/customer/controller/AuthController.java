package com.stalwart.customer.controller;

import com.stalwart.customer.model.AuthenticationRequest;
import com.stalwart.customer.model.AuthenticationResponse;
import com.stalwart.customer.model.OTPRequest;
import com.stalwart.customer.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;

import java.security.SecureRandom;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final JavaMailSender javaMailSender;
    private final AuthService authService;

    public AuthController(JavaMailSender javaMailSender, AuthService authService) {
        this.javaMailSender = javaMailSender;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request){
        AuthenticationResponse authenticationResponse = authService.login(request);
        return  ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,authenticationResponse.token())
                .build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        // Generate a random OTP
        String otp = generateRandomOTP();

        // Send OTP via email
        boolean emailSent = sendOTPEmail(email, otp);

        // Store OTP in the database with a timestamp
        if (emailSent) {
            storeOTPInDatabase(email, otp);
            return ResponseEntity.ok("OTP sent successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP");
        }
    }

    private void storeOTPInDatabase(String email, String otp) {
        authService.storeOtp(email, otp);
    }

    private String generateRandomOTP() {
        final String OTP_CHARACTERS = "0123456789"; // Characters allowed in the OTP
        final int OTP_LENGTH = 6; // Length of the OTP
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int randomIndex = random.nextInt(OTP_CHARACTERS.length());
            char randomChar = OTP_CHARACTERS.charAt(randomIndex);
            otp.append(randomChar);
        }
        return otp.toString();
    }
    private boolean sendOTPEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("OTP for Password Reset");
            message.setText("Your OTP is: " + otp + "\n OTP will expire within 30 seconds.");
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/submit-otp")
    public ResponseEntity<String> sumitOTP(@RequestBody OTPRequest request) {

        String email = request.email();
        String otp = request.otp();

        boolean isValidOTP = authService.validateOTP(email, otp, System.currentTimeMillis());
        if (isValidOTP) {
            return ResponseEntity.ok("OTP Validation Successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect OTP");
        }
    }
}
