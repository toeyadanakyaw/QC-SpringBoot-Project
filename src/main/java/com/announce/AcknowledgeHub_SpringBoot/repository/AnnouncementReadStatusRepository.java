package com.announce.AcknowledgeHub_SpringBoot.repository;

import com.announce.AcknowledgeHub_SpringBoot.entity.Announcement;
import com.announce.AcknowledgeHub_SpringBoot.entity.AnnouncementReadStatus;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementReadStatusRepository extends JpaRepository<AnnouncementReadStatus, Integer> {
    AnnouncementReadStatus findByAnnouncementAndStaff(Announcement announcement, User user);

    AnnouncementReadStatus findByAnnouncementId(Integer id);

    List<AnnouncementReadStatus> findAllByAnnouncementId(int announcementId);

    List<AnnouncementReadStatus> findByAnnouncementId(int announcementId);

    // Method to count the total number of users to whom the announcement was sent
    int countByAnnouncement(Announcement announcement);

    // Method to count the number of users who have read a specific announcement
    int countByAnnouncementAndIsReadTrue(Announcement announcement);
}
