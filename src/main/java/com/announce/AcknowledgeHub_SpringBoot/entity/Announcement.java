package com.announce.AcknowledgeHub_SpringBoot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "announcement")
public class Announcement {
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

     @Lob
     @Column(name = "document")
     private byte[] document;

     @Column(name = "document_name")
     private String documentName;

     @Column(name = "sent")
     private int sent;

     @Column(name = "scheduled_date")
     private LocalDateTime scheduledDate;

     @Column(name = "cloudUrl")
     private String cloudUrl;

     @Column(name = "fileExtension")
     private String fileExtension;

     @Column(name = "public_id")
     private String publicId;

     @Column(name = "resource_type")
     private String resourceType;

     @ManyToMany(fetch = FetchType.EAGER)
     @JoinTable(
             name = "announcement_group",
             joinColumns = @JoinColumn(name = "announcement_id"),
             inverseJoinColumns = @JoinColumn(name = "group_id")
     )
     private List<Group> groups;

     @OneToMany(fetch = FetchType.EAGER,
             mappedBy = "announcement",
             cascade = CascadeType.ALL,
             orphanRemoval = true)
     private List<AnnouncementReadStatus> staffMembers;

     @ManyToOne
     @JoinColumn(name = "user_id")
     private User user;
}
