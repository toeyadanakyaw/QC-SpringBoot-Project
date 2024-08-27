package com.announce.AcknowledgeHub_SpringBoot.exception;

public class RegistrationException extends RuntimeException{
    public RegistrationException(String message, Exception e) {
        super(message);
    }
}
