package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.entity.Group;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.model.GroupCreationDto;
import com.announce.AcknowledgeHub_SpringBoot.repository.GroupRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import com.announce.AcknowledgeHub_SpringBoot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin()
@RequestMapping("api/group")
public class GroupController {

    private final GroupService groupService;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    @Autowired
    public GroupController(GroupRepository groupRepository, UserRepository userRepository, GroupService groupService){
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupService = groupService;
    }

    @GetMapping("/staff")
    public List<User> getAllStaffs(){
        return userRepository.findAll();
    }

    @PostMapping
    public Group createGroup(@RequestBody GroupCreationDto groupCreationDto){
        return groupService.createGroup(groupCreationDto);
    }

    @PostMapping("/{groupId}/add-staff")
    public Group addStaffToGroup(@PathVariable int groupId, @RequestBody List<Integer> staffIds) {
        return groupService.addStaffToGroup(groupId, staffIds);
    }

//    @GetMapping("/getAllGroup")
//    public List<Group> getAllGroup(@PathVariable String id) {
//        return groupRepository.findAll();
//    }
    @GetMapping("/getAllGroup")
    public List<Group> getAllGroup(@RequestParam String role,  @RequestParam(required = false) String userId ) {
        System.out.println("Group is here!!");
        System.out.println("Group userId:"+userId);
        return groupService.getAllGroup(role, userId);
    }
}
