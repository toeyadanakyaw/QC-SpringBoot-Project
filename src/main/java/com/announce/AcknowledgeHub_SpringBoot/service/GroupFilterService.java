package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.entity.Company;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.repository.CompanyRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

@Service
public class GroupFilterService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public GroupFilterService(UserRepository userRepository, CompanyRepository companyRepository){
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public List<User> getAllUsers(String role, Integer companyId, Integer departmentId) {
        List<User> users;

        if ("MAIN_HR".equals(role)) {
            users = userRepository.findAllByStatus(true);
        } else if ("SUB_HR".equals(role) && companyId != null) {
            users = userRepository.findByCompanyIdAndStatus(companyId, true);
        } else if ("MANAGEMENT".equals(role)) {
            if (isUserCEO()) { // CEO sees all users
                users = userRepository.findAllByStatus(true);
            } else if (companyId != null && departmentId != null) {
                users = userRepository.findByCompanyIdAndDepartmentIdAndStatus(companyId, departmentId, true);
            } else {
                throw new RuntimeException("Unauthorized access or insufficient parameters.");
            }
        } else {
            throw new RuntimeException("Unauthorized access or company ID not provided.");
        }

        if (users.isEmpty()) {
            throw new RuntimeException("No users found.");
        }
        return users;
    }

    private boolean isUserCEO() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return "CEO".equals(currentUser.getPosition());
    }

    public List<Company> getAllCompany(String role, Integer id){
        List<Company> companies;

        if ("MAIN_HR".equals(role)){
            companies = companyRepository.findAll();
        }
         else if ("SUB_HR".equals(role) && id != 0){
            Optional<Company> optionalCompany = companyRepository.findById(id);
            if (optionalCompany.isPresent()) {
                companies = List.of(optionalCompany.get());
            } else {
                companies = List.of(); // return an empty list if the company is not found
            }
        } else {
            throw new RuntimeException("Company is not available");
        }
         return companies;
    }

}
