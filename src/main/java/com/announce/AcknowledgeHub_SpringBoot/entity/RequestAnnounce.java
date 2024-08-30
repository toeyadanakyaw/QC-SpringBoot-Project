package com.announce.AcknowledgeHub_SpringBoot.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requestAnnounce")
public class RequestAnnounce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(name = "cloudUrl")
    private String cloudUrl;

    @Column(name = "fileExtension")
    private String fileExtension;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "resource_type")
    private String resourceType;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status;

    // Define the enum inside the class
    public enum Status {
        SOFT_DELETE, APPROVE, DECLINE, PENDING
    }
}
