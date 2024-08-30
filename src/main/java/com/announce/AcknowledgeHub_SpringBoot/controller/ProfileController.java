package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.model.UserDTO;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class ProfileController {

    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private final String folderName = "YNWA";

    public ProfileController(UserRepository userRepository, Cloudinary cloudinary){
        this.cloudinary = cloudinary;
        this.userRepository = userRepository;
    }

    @PutMapping("/upload-profile-photo")
    public ResponseEntity<Map> uploadProflePhoto(@RequestParam("file") MultipartFile file, @RequestParam("userId") int userId, boolean overwrite) throws IOException {
        System.out.println("File Upload Here:"+file+userId);
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String fileNameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        String publicId = folderName + "/" + fileNameWithoutExtension + (overwrite ? "" : "_" + System.currentTimeMillis());
        String resourceType = "auto"; // Automatically detect resource type

        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "public_id", publicId,
                "resource_type", resourceType
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        String cloudUrl = (String) uploadResult.get("secure_url");
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPhotoUrl(cloudUrl);
        user.setFileExtension(fileExtension);
        userRepository.save(user);
        System.out.println("UPLOA SUCCESFUL:"+user);

        return ResponseEntity.ok(Map.of("url", cloudUrl));
    }

    private String getFileExtension(String filename) {
        String fileExtension = "";
        if (filename != null && filename.lastIndexOf('.') > 0) {
            fileExtension = filename.substring(filename.lastIndexOf('.'));
        }
        return fileExtension;
    }

    @GetMapping("/profile/{id}")
    public User getUserProfile(@PathVariable int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @DeleteMapping("/profile/photo/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPhotoUrl(null);  // Remove the photo URL from the user
        user.setFileExtension(null);
        userRepository.save(user);  // Update the user record in the database
        return ResponseEntity.noContent().build();  // Return a no content response
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable int id, @RequestBody UserDTO userDTO){
        System.out.println("Profile data:"+userDTO);
        User existUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existUser.setName(userDTO.getName());
        existUser.setEmail(userDTO.getEmail());
        existUser.setPh_number(userDTO.getPh_number());
        User updateUser =userRepository.save(existUser);

        return ResponseEntity.ok(updateUser);
    }

}
