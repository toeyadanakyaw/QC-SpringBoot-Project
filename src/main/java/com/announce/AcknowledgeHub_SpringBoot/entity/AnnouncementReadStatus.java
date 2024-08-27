package com.announce.AcknowledgeHub_SpringBoot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class AnnouncementReadStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User staff;

    private Integer messageId; // Add this field to store message ID

    private LocalDateTime readAt;

    private Boolean isRead = false;

}
