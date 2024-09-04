package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.entity.Comment;
import com.announce.AcknowledgeHub_SpringBoot.model.CommentDTO;
import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import com.announce.AcknowledgeHub_SpringBoot.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/announcement/{announcementId}")
    public ResponseEntity<List<Comment>> getCommentsByAnnouncementId(@PathVariable int announcementId) {
        List<Comment> comments = commentService.getCommentsByAnnouncementId(announcementId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/writeComment")
    public ResponseEntity<Comment> addComment(@RequestBody CommentDTO commentDto) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setAnnouncement(announcementRepository.findById((long) commentDto.getAnnouncementId()).orElseThrow(() -> new RuntimeException("Announcement not found")));
        comment.setUser(userRepository.findById(commentDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found")));
        Comment savedComment = commentService.addComment(comment);
        return ResponseEntity.ok(savedComment);
    }

}
