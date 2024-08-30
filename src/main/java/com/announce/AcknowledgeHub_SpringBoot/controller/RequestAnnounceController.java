package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.entity.Announcement;
import com.announce.AcknowledgeHub_SpringBoot.entity.RequestAnnounce;
import com.announce.AcknowledgeHub_SpringBoot.service.RequestAnnounceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")  // Replace with your frontend URL
@RestController
@RequestMapping("/api/request-announce")
public class RequestAnnounceController {

    @Autowired
    private RequestAnnounceService requestAnnounceService;

    @PostMapping("/create")
    public ResponseEntity<?> createRequestAnnounce(
            @RequestPart("requestAnnounce") RequestAnnounce requestAnnounce,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "overwrite", defaultValue = "false") boolean overwrite) {
        System.out.println("RequestAnnounce: " + requestAnnounce);
        System.out.println("File: " + (file != null ? file.getOriginalFilename() : "No file attached"));

        try {
            RequestAnnounce createdRequestAnnounce = requestAnnounceService.createRequestAnnouncement(requestAnnounce, file, overwrite);
            return new ResponseEntity<>(createdRequestAnnounce, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/findall")
    public ResponseEntity<List<RequestAnnounce>> findAll() {
        List<RequestAnnounce> requestAnnounces = requestAnnounceService.findAll();
        return ResponseEntity.ok(requestAnnounces);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateAnnouncementStatus(@PathVariable int id, @RequestBody Map<String, Integer> statusUpdate) {
        try {
            int newStatus = statusUpdate.get("status");

            // Update the status of the RequestAnnounce
            RequestAnnounce updatedAnnounce = requestAnnounceService.updateStatus(id, newStatus);
            if (updatedAnnounce.getStatus() == RequestAnnounce.Status.APPROVE) {
                System.out.println("Approved status detected, creating an announcement...");
                Announcement createdAnnouncement = requestAnnounceService.createAnnouncementFromRequest(updatedAnnounce);
                return ResponseEntity.ok(createdAnnouncement);
            }

            return ResponseEntity.ok(updatedAnnounce);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update announcement status or create announcement");
        }
    }
}
