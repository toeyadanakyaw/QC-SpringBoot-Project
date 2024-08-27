package com.announce.AcknowledgeHub_SpringBoot.model;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String token;
    private String message;



}
