package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.entity.Company;
import com.announce.AcknowledgeHub_SpringBoot.entity.Department;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.repository.CompanyRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.DepartmentRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/api/groupFilter")
public class GroupFilterController {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public GroupFilterController(CompanyRepository companyRepository, UserRepository userRepository, DepartmentRepository departmentRepository){
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/company")
    public List<Company> getCompany(){
        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()){
            throw new RuntimeException("No companies found.");
        }
        return companies;
    }

    @GetMapping("/department")
    public List<Department> getAllDepartment(){
        List<Department> departments = departmentRepository.findAll();
        if (departments.isEmpty()){
            throw new RuntimeException("No department found");
        }
        return departments;
    }

    @GetMapping("get-user")
    public List<User> getAllUser(){
        List<User> users = userRepository.findAll();
        if (users.isEmpty()){
            throw new RuntimeException("No users found.");
        }
        return users;
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

}
