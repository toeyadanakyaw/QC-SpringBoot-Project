package com.announce.AcknowledgeHub_SpringBoot.repository;

import com.announce.AcknowledgeHub_SpringBoot.entity.Group;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Integer> {

//    List<Group> findAllByAnnouncementId(Integer announcementId);

    @Query("select u from Group u where u.name=?1")
    Optional<Group> findByGroupName(String groupName);

    @Query("SELECT g FROM Group g WHERE REPLACE(LOWER(g.name), ' ', '') = REPLACE(LOWER(:name), ' ', '')")
    Optional<Group> findByNameIgnoreCaseAndSpaces(@Param("name") String name);

    //    @Query("SELECT s FROM Staff s JOIN s.groups g WHERE g.id = :groupId")
//    List<Staff> findAllStaffByGroupId(@Param("groupId") int groupId);
    //for user table
    @Query("SELECT s FROM User s JOIN s.groups g WHERE g.id = :groupId")
    List<User> findAllStaffByGroupId(@Param("groupId") int groupId);
}
