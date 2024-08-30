package com.announce.AcknowledgeHub_SpringBoot.repository;

import com.announce.AcknowledgeHub_SpringBoot.entity.RequestAnnounce;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestAnnounceRepository extends JpaRepository<RequestAnnounce, Integer> {
}