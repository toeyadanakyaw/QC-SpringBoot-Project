package com.announce.AcknowledgeHub_SpringBoot.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class AnnouncementDTO {
    private int id;
    private  String title;
    private String content;
    private LocalDateTime created_at;
    private byte[] document;
    private String documentName;
    private int sent;
    private LocalDateTime scheduledDate;
    private List<Integer> groupIds; // Store only the IDs of the groups
    private List<Integer> staffIds;
    private int user_id;
    private String userName;
}
