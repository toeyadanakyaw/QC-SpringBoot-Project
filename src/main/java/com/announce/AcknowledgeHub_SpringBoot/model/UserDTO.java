package com.announce.AcknowledgeHub_SpringBoot.model;

import lombok.Data;

@Data
public class UserDTO {

    private String name;
    private int id;
    private String email;
    private String password;
    private byte[] photo;
    private Role role;
    private boolean state;

}
