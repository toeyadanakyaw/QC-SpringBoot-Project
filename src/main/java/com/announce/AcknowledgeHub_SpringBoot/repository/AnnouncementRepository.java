package com.announce.AcknowledgeHub_SpringBoot.repository;

import com.announce.AcknowledgeHub_SpringBoot.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Optional<Announcement> findByPublicId(String publicId);

    @Query("SELECT COUNT(a) FROM Announcement a")
    int countAllAnnouncements();

    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.sent = 0")
    int countUpcomingAnnouncements();

    @Query("SELECT FUNCTION('DATE_FORMAT', a.createdAt, '%Y-%m') AS period, COUNT(a) FROM Announcement a GROUP BY period")
    List<Object[]> countTotalAnnouncementsByMonth();

    @Query("SELECT FUNCTION('DATE_FORMAT', a.createdAt, '%Y') AS period, COUNT(a) FROM Announcement a GROUP BY period")
    List<Object[]> countTotalAnnouncementsByYear();

    @Query("SELECT d.name, COUNT(a) FROM Announcement a JOIN a.user u JOIN u.department d GROUP BY d.name")
    List<Object[]> countAnnouncementsByDepartment();
}


