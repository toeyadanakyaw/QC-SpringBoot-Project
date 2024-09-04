package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.entity.Company;
import com.announce.AcknowledgeHub_SpringBoot.entity.Department;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.repository.CompanyRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.DepartmentRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import com.announce.AcknowledgeHub_SpringBoot.service.GroupFilterService;
import com.announce.AcknowledgeHub_SpringBoot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/api/groupFilter")
public class GroupFilterController {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final GroupService groupService;
    private final GroupFilterService groupFilterService;

    @Autowired
    public GroupFilterController(CompanyRepository companyRepository, UserRepository userRepository, DepartmentRepository departmentRepository, GroupService groupService, GroupFilterService groupFilterService){
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.groupService = groupService;
        this.groupFilterService = groupFilterService;
    }

//    @GetMapping("/company")
//    public List<Company> getCompany(){
//        List<Company> companies = companyRepository.findAll();
//        if (companies.isEmpty()){
//            throw new RuntimeException("No companies found.");
//        }
//        return companies;
//    }
      @GetMapping("/company")
      public List<Company> getCompany(@RequestParam String role, @RequestParam(required = false) Integer id){
          System.out.println("Company is here");
          System.out.println("Company Id:"+id);
        return groupFilterService.getAllCompany(role, id);
      }

    @GetMapping("/department")
    public List<Department> getAllDepartment(){
        List<Department> departments = departmentRepository.findAll();
        if (departments.isEmpty()){
            throw new RuntimeException("No department found");
        }
        return departments;
    }

//    @GetMapping("get-user")
//    public List<User> getAllUser(){
//        List<User> users = userRepository.findAll();
//        if (users.isEmpty()){
//            throw new RuntimeException("No users found.");
//        }
//        return users;
//    }

//    @GetMapping("get-user")
//    public List<User> getAllUser(@RequestParam String role,
//                                 @RequestParam(required = false) Integer companyId,
//                                 @RequestParam(required = false) Integer departmentId){
//        List<User> users;
//
//        if ("MAIN_HR".equals(role)){
//            users = userRepository.findAllByStatus(true);
//        } else if ("SUB_HR".equals(role) && companyId != null) {
//            users = userRepository.findByCompanyIdAndStatus(companyId, true);
//        } else if ("MANAGEMENT".equals(role) && companyId != null && departmentId != null) {
//            users = userRepository.findByCompanyIdAndDepartmentIdAndStatus(companyId, departmentId, true);
//        }else {
//            throw new RuntimeException("Unauthorized access or company ID not provided.");
//        }
//
//        if (users.isEmpty()){
//            throw new RuntimeException("No users found.");
//        }
//        return users;
//    }

    @GetMapping("get-user")
    public List<User> getAllUser(@RequestParam String role,
                                 @RequestParam(required = false) Integer companyId,
                                 @RequestParam(required = false) Integer departmentId) {
        return groupFilterService.getAllUsers(role, companyId, departmentId);
    }

    @GetMapping("/department/{companyId}")
    public List<Department> getDepartmentByCompanyId(@PathVariable int companyId){
        List<Department> departments = departmentRepository.findByCompanyId(companyId);
        if (departments.isEmpty()){
            throw new RuntimeException("No department found.");
        }
        return departments;
    }

    @GetMapping("get-user/{companyId}")
    public List<User> getUserByCompanyId(@PathVariable int companyId){
        List<User> users = userRepository.findByCompanyId(companyId);
        if (users.isEmpty()){
            throw new RuntimeException("No found user");
        }
        return users;
    }

    @GetMapping("get-user/department/{departmentId}/company/{companyId}")
    public List<User> getUserByDepartmentIdandCompanyId(@PathVariable int departmentId, @PathVariable int companyId){
        List<User> users = userRepository.findByDepartmentIdAndCompanyId(departmentId,companyId);
        if (users.isEmpty()){
            throw new RuntimeException("No user found");
        }
        return users;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable int id){
        try {
            groupService.deleteGroup(id);
            return ResponseEntity.ok("Group delete successful");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

}
