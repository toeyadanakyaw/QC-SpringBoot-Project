//package com.announce.AcknowledgeHub_SpringBoot.repository;
//
//import com.announce.AcknowledgeHub_SpringBoot.entity.Staff;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface StaffRepository extends JpaRepository<Staff, Integer> {
//
//        @Query("SELECT s.email FROM Staff s WHERE s.group.id = :groupId")
//        List<String> findEmailsByGroupId(@Param("groupId") int groupId);
//
//}
