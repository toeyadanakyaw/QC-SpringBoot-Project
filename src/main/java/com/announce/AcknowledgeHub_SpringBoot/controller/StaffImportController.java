package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.service.ImportStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/excel")
public class StaffImportController {

    @Autowired
    ImportStaffService importStaffService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            importStaffService.importExcel(file);
            return ResponseEntity.ok("File imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to import file: " + e.getMessage());
        }
    }
}
