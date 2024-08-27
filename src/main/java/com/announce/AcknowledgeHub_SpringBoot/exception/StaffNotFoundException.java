package com.announce.AcknowledgeHub_SpringBoot.exception;

public class StaffNotFoundException extends RuntimeException{
    public StaffNotFoundException(String message) {
        super(message);
    }
}
