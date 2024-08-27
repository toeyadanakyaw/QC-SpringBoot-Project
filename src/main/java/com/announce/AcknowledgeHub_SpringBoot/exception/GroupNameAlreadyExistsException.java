package com.announce.AcknowledgeHub_SpringBoot.exception;

public class GroupNameAlreadyExistsException extends RuntimeException{

    public GroupNameAlreadyExistsException(String message){
        super(message);
    }

}
