package com.announce.AcknowledgeHub_SpringBoot.model;

import lombok.Data;

@Data
public class CommentDTO {
    private String content;
    private int announcementId;
    private int userId; // Only include essential user information
}
