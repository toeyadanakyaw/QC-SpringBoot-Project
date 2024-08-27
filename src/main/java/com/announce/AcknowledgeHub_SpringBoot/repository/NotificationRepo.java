package com.announce.AcknowledgeHub_SpringBoot.repository;

import com.announce.AcknowledgeHub_SpringBoot.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<Notification,Integer> {
}
