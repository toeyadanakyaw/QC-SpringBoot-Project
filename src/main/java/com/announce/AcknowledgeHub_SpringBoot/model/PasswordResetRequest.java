package com.announce.AcknowledgeHub_SpringBoot.model;

import lombok.Data;

@Data
public class PasswordResetRequest {
    String email;
    String newPassword;
}
