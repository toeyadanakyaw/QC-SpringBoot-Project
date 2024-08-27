package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    // Class to store OTP and its expiration time
    private static class OtpEntry {
        private final String otp;
        private final String email;
        private final LocalDateTime expirationTime;

        public OtpEntry(String otp, String email, LocalDateTime expirationTime) {
            this.otp = otp;
            this.email = email;
            this.expirationTime = expirationTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }
        public String getEmail(){
            return email;
        }
    }

    private final ConcurrentHashMap<String, OtpEntry> otpCache = new ConcurrentHashMap<>();
    private final int OTP_VALID_DURATION = 3; // OTP is valid for 3 minutes

    // Generate OTP and store with expiration time
    public String generateOtp(String email) {
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(OTP_VALID_DURATION);

        otpCache.put(email, new OtpEntry(otp,email,expirationTime));
        // Log the generated OTP and expiration time
        System.out.println("Generated OTP: " + otp + " for email: " + email + ", expires at: " + expirationTime);

        return otp;
    }

    // Validate OTP and check if it is within the valid duration
    public boolean validateOtp(String email, String otp) {

        OtpEntry otpEntry = otpCache.get(email);
        if (otpEntry == null) {
            System.out.println("No OTP found for email: " + email);
            return false;
        }

        // Log the validation process
        System.out.println("Validating OTP: " + otp + " for email: " + email + ", stored OTP: " + otpEntry.getOtp() + ", expires at: " + otpEntry.getExpirationTime());

        // Check if OTP is valid and has not expired
        if (LocalDateTime.now().isAfter(otpEntry.getExpirationTime())) {
            otpCache.remove(email); // Remove expired OTP
            System.out.println("OTP expired for email: " + email);
            return false;
        }

        boolean isValid = otp.equals(otpEntry.getOtp());
        System.out.println("Is OTP valid? " + isValid);
        return isValid;
    }

    // Clear the OTP after successful validation
    public void clearOtp(String email) {
        otpCache.remove(email);
        System.out.println("OTP cleared for email: " + email);
    }
}
