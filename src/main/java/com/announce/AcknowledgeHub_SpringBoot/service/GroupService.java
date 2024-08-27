package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.entity.Group;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.exception.GroupNameAlreadyExistsException;
import com.announce.AcknowledgeHub_SpringBoot.model.GroupCreationDto;
import com.announce.AcknowledgeHub_SpringBoot.repository.GroupRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequestMapping("/api/group")
public class GroupService {

    @Autowired
    GroupRepository groupRepository;

//    @Autowired
//    StaffRepository staffRepository;

    @Autowired
    UserRepository userRepository;

    public Optional<Group> findByName(String groupName){
        return groupRepository.findByGroupName(groupName);
    }

    @Transactional
    @PostMapping("/create")
    public Group createGroup(GroupCreationDto groupCreationDto){
        String normalizedGroupName = groupCreationDto.getGroupName().replaceAll("\\s", "").toLowerCase();

        //when many duplicate name in database
//        List<Group> existingGroups = groupRepository.findByNameIgnoreCaseAndSpaces(normalizedGroupName);

        // Check if a group with the same normalized name already exists
        if (groupRepository.findByNameIgnoreCaseAndSpaces(normalizedGroupName).isPresent()) {
            throw new GroupNameAlreadyExistsException("Group name already exists");
        }

        Group group = new Group();
        group.setName(groupCreationDto.getGroupName());
        //for many to one
//        group = groupRepository.save(group);

        //for many to many
//        Set<Staff> staffs = new HashSet<>();
        //many to many for user table
        Set<User> staffs = new HashSet<>();

        List<Integer> staffIds = groupCreationDto.getStaffIds();
        if (staffIds != null) {
            for (Integer staffId : staffIds) {
                //for staff table
//                Staff staff = staffRepository.findById(staffId).orElse(null);
                //for user table
                User staff = userRepository.findById(staffId).orElse(null);
                if (staff != null) {
                    //for many to one
//                    staff.setGroup(group);
//                  if (staff != null && !group.getStaffs().contains(staff)){
                      //for many to many
                      staffs.add(staff);
                  }

                    //for many to many
//                    staffRepository.save(staff);

            }
        }
        //for staff table
//        group.setStaffs(staffs);
        //for user table
        group.setUsers(staffs);

        // for one to many
//        return group;

        //for many to many
        return groupRepository.save(group);

    }

    @PostMapping("/add-staff")
    public Group addStaffToGroup(int groupId, List<Integer> staffIds) {

        // Fetch the group by ID or throw an exception if not found
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        System.out.println("Group ID: " + groupId);

        // Iterate through the provided staff IDs
        for (Integer staffId : staffIds) {
            // Find the staff by ID
//            Staff staff = staffRepository.findById(staffId).orElse(null);
            //for user table
            User staff = userRepository.findById(staffId).orElse(null);

            if (staff != null) {
                // Check if the staff is not already a member of the group
//                if (!group.getStaffs().contains(staff)) {
//                    // Add the staff to the group
//                    group.getStaffs().add(staff);
                //for user table
                if (!group.getUsers().contains(staff)) {
                    // Add the staff to the group
                    group.getUsers().add(staff);
                } else {
                    // If the staff is already in the group, log a message
                    System.out.println("Staff with ID " + staffId + " is already a member of this group.");
                }
            } else {
                // If staff doesn't exist, log a message
                System.out.println("Staff with ID " + staffId + " does not exist.");
            }
        }

        // Save the group after all staff members have been added
        groupRepository.save(group);

        // Return the updated group
        return group;
    }

}
