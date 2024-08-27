package com.announce.AcknowledgeHub_SpringBoot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "request")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "request_date")
    private Date request_date;

    @Column(name = "content")
    private String content;

//    @ManyToOne
//    @JoinColumn(name = "staff_id")
//    private Staff staff;
    //for user table

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

}
