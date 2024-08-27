package com.announce.AcknowledgeHub_SpringBoot.model;

import lombok.Data;

@Data
public class OtpVerificationRequest {

    private String email;
    private String otp;

}
