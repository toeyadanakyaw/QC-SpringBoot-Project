package com.announce.AcknowledgeHub_SpringBoot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at")
    private Date created_at;

    @Column(name = "is_seen")
    private boolean is_seen;

    @ManyToOne
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

//    @ManyToOne
//    @JoinColumn(name = "staff_id")
//    private Staff staff;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
