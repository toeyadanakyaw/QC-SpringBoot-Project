package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.entity.Comment;
import com.announce.AcknowledgeHub_SpringBoot.model.CommentDTO;
import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import com.announce.AcknowledgeHub_SpringBoot.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentDTO commentDto) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setAnnouncement(announcementRepository.findById((long) commentDto.getAnnouncementId())
                .orElseThrow(() -> new RuntimeException("Announcement not found")));
        comment.setUser(userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));

        if (commentDto.getParentCommentId() != null) {
            // Fetch parent comment if this is a reply
            Comment parentComment = commentService.getCommentById(commentDto.getParentCommentId());
            comment.setParentComment(parentComment);
        }

        Comment savedComment = commentService.addComment(comment);
        return ResponseEntity.ok(savedComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable int id, @RequestBody CommentDTO commentDto) {
        Comment existingComment = commentService.getCommentById(id);
        existingComment.setContent(commentDto.getContent());
        existingComment.setEdited(true); // Mark as edited
        existingComment.setUpdatedAt(LocalDateTime.now()); // Update the edited date
        Comment updatedComment = commentService.addComment(existingComment);
        return ResponseEntity.ok(updatedComment); // Return updated comment
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable int id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 if the comment is not found
        }
    }

}
