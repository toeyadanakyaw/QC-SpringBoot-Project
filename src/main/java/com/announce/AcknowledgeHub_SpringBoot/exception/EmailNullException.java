package com.announce.AcknowledgeHub_SpringBoot.exception;

public class EmailNullException extends RuntimeException{
    public EmailNullException(String message) {
        super(message);
    }
}
