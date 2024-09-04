package com.announce.AcknowledgeHub_SpringBoot.repository;

import com.announce.AcknowledgeHub_SpringBoot.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByAnnouncementId(int announcementId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.replies WHERE c.announcement.id = :announcementId AND c.parentComment IS NULL")
    List<Comment> findByAnnouncementIdAndParentCommentIsNull(int announcementId);

}
