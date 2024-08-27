//package com.announce.AcknowledgeHub_SpringBoot.controller;
//
//import com.announce.AcknowledgeHub_SpringBoot.entity.User;
//import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
//import jdk.dynalink.linker.LinkerServices;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//@RestController
//@CrossOrigin(origins = "http://localhost:4200")
//@RequestMapping("/api/download")
//public class DownloadController {
//
//    private final UserRepository userRepository;
//
//    @Autowired
//    public DownloadController(UserRepository userRepository){
//        this.userRepository = userRepository;
//    }
//
//    @GetMapping("/csv")
//    public ResponseEntity<InputStreamResource> downnLoadUserList() throws IOException{
//        List<User> users = userRepository.findAll();
//
//    }
//}
