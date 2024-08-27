package com.announce.AcknowledgeHub_SpringBoot.repository;

import com.announce.AcknowledgeHub_SpringBoot.entity.Announcement;
import com.announce.AcknowledgeHub_SpringBoot.entity.AnnouncementReadStatus;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementReadStatusRepository extends JpaRepository<AnnouncementReadStatus, Integer> {
    AnnouncementReadStatus findByAnnouncementAndStaff(Announcement announcement, User user);

    AnnouncementReadStatus findByAnnouncementId(Integer id);
}
