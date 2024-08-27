package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.model.AuthenticationResponse;
import com.announce.AcknowledgeHub_SpringBoot.model.OtpVerificationRequest;
import com.announce.AcknowledgeHub_SpringBoot.model.PasswordResetRequest;
import com.announce.AcknowledgeHub_SpringBoot.model.UserDTO;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import com.announce.AcknowledgeHub_SpringBoot.service.AuthenticationService;
import com.announce.AcknowledgeHub_SpringBoot.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("api/user")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JavaMailSender javaMailSender;

    @PutMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserDTO request) {
        System.out.println("Register data"+ request.getEmail() + request.getPassword());
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (RuntimeException e) {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> login(@RequestBody UserDTO request) {
        System.out.println("Received login request: " + request);
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (RuntimeException e) {
            System.out.println("Error during authentication: " + e.getMessage());
            AuthenticationResponse response = new AuthenticationResponse();
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody UserDTO dto) {
        // Log the email being passed
        System.out.println("Received email: " + dto.getEmail());

        User user = userRepository.findEmail(dto.getEmail());
        System.out.println("user"+user);

        if (user == null) {
            System.out.println("User not found for email: " + dto.getEmail());
            Map<String, String> response = new HashMap<>();
            response.put("message", "User with this email does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        String otp = otpService.generateOtp(dto.getEmail());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getEmail());
        message.setText(otp);
        javaMailSender.send(message);

        System.out.println("OTP sent to email: " + dto.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP has been sent to your email.");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest dto) {
        System.out.println("Verifying OTP for email: " + dto.getEmail() + ", provided OTP: " + dto.getOtp());

        boolean isValid = otpService.validateOtp(dto.getEmail(), dto.getOtp());

        if (isValid) {
            otpService.clearOtp(dto.getEmail());
            System.out.println("OTP is valid for email: " + dto.getEmail());
            return ResponseEntity.ok("OTP is valid.");
        } else {
            System.out.println("Invalid or expired OTP for email: " + dto.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP.");
        }
    }
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        System.out.println("request: " + request);
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password cannot be empty.");
        }

        boolean result = authenticationService.resetPassword(request.getEmail(), request.getNewPassword());

        if (result) {
            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password reset failed.");
        }
    }


}

