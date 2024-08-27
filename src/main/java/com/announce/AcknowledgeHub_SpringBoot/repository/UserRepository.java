package com.announce.AcknowledgeHub_SpringBoot.repository;

import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);  // Updated to match the property name in UserEntity

    @Query("select u from User u where u.email=?1")
    User findEmail(String email);

    @Query("select s from User s where s.email=?1")
    User findDataByEmail(String email);

    @Query("SELECT u.email FROM User u JOIN u.groups g WHERE g.id = :groupId")
    List<String> findEmailsByGroupId(@Param("groupId") int groupId);

    @Query("SELECT u.telegram_user_id FROM User u JOIN u.groups g WHERE g.id = :groupId")
    List<Long> findTelegramUserIdByGroupId(@Param("groupId") int groupId);


    //my code
    List<User> findByName(String userName);

    @Query("SELECT s FROM User s WHERE s.name = :username")
    User findByNameOne(@Param("username") String username);

    @Query("select c from User c where c.registration_code = ?1")
    User findByRegistrationCode(String registrationCode);

//    List<User> findByTelegramUserId(Long telegramUserId);

    @Query("SELECT s FROM User s WHERE s.telegram_user_id = :telegramUserId")
    User findByTelegramUserIdToGetUName(@Param("telegramUserId") Long telegramUserId);

    List<User> findByCompanyId(int companyId);

    List<User> findByDepartmentIdAndCompanyId(int departmentId, int companyId);










}
