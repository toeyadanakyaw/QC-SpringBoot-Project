package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("api/search")
public class SearchController {

    private final UserRepository userRepository;

    public SearchController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/byName")
    public List<User> searchByName(@RequestParam(value = "query", required = false) String query) {
        List<User> users = userRepository.findAll();

        if (query == null || query.isEmpty()){
            return users;
        }

        String lowerCaseQuery = query.toLowerCase();

        return users.stream()
                .filter(user ->
                        user.getName().toLowerCase().contains(lowerCaseQuery) ||
                        user.getDepartment().getName().toLowerCase().contains(lowerCaseQuery) ||
                        user.getCompany().getName().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());

   }
}
