package com.announce.AcknowledgeHub_SpringBoot.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private String content;
    private int announcementId;
    private int userId; // Only include essential user information
    private Integer parentCommentId; // Optional parent comment ID for replies
    private LocalDateTime updatedAt; // Optional field to show when a comment was last updated
}
