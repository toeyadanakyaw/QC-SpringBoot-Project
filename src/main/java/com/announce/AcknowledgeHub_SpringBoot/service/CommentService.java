package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.entity.Comment;
import com.announce.AcknowledgeHub_SpringBoot.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentsByAnnouncementId(int announcementId) {
        return commentRepository.findByAnnouncementId(announcementId);
    }

    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }
}
