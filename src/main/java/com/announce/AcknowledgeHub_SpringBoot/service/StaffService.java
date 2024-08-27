package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.entity.Group;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.repository.GroupRepository;

import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    @Autowired
//    private final StaffRepository staffRepository;
    //for user table
    private final UserRepository staffRepository;
    private final GroupRepository groupRepository;

//    public StaffService(StaffRepository staffRepository, GroupRepository groupRepository){
    //for user table
public StaffService(UserRepository staffRepository, GroupRepository groupRepository){
        this.staffRepository = staffRepository;
        this.groupRepository = groupRepository;
    }

//    public List<String> getEmailsByGroupId(int groupId) {
//        return staffRepository.findEmailsByGroupId(groupId);
//    }
    //for user table
public List<String> getEmailsByGroupId(int groupId) {
    return staffRepository.findEmailsByGroupId(groupId);
}
//for user table
public List<Long> getTelegramUserIdByGroupId(int groupId) {
    return staffRepository.findTelegramUserIdByGroupId(groupId);
}

//    public Optional<Staff> getStaffById(int id){
//        return staffRepository.findById(id);
//    }
    //for user table
public Optional<User> getStaffById(int id){
    return staffRepository.findById(id);
}

    // Retrieve groups by a list of group IDs
    public List<Group> getGroupsByIds(List<Integer> groupIds) {
        return groupRepository.findAllById(groupIds);
    }

    // Retrieve staff members by a list of staff IDs
//    public List<Staff> getStaffByIds(List<Integer> staffIds) {
//        return staffRepository.findAllById(staffIds);
//    }
    //for user table
    public List<User> getStaffByIds(List<Integer> staffIds) {
        return staffRepository.findAllById(staffIds);
    }
}
