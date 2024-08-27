package com.announce.AcknowledgeHub_SpringBoot.model;

import lombok.Data;

@Data
public class StaffDTO {
    private int id;
    private String name;
    private String email;
    private String ph_number;
    private Role role;
    private int group_id;
}
