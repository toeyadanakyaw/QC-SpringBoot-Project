package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.entity.Announcement;
import com.announce.AcknowledgeHub_SpringBoot.entity.RequestAnnounce;
import com.announce.AcknowledgeHub_SpringBoot.repository.RequestAnnounceRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.google.common.io.Files.getFileExtension;

@Service
public class RequestAnnounceService {
    private final StaffService staffService;
    private final Cloudinary cloudinary;
    private final String folderName = "YNWA";
    @Autowired
    private RequestAnnounceRepository repo;
    @Autowired
    private AnnouncementService announcementService;
    public RequestAnnounceService(StaffService staffService, Cloudinary cloudinary) {
        this.staffService = staffService;
        this.cloudinary = cloudinary;
    }

    public RequestAnnounce createRequestAnnouncement(RequestAnnounce requestAnnounce, MultipartFile file, boolean overwrite) throws IOException {
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
        requestAnnounce.setPublicId(publicId);
        requestAnnounce.setResourceType(resourceType);
        requestAnnounce.setFileExtension(fileExtension); // Store file extension in the announcement

        String cloudUrl = (String) uploadResult.get("secure_url");
        requestAnnounce.setCloudUrl(cloudUrl);

        System.out.println("File uploaded to Cloudinary. Public ID: " + publicId + ", URL: " + cloudUrl);
        return repo.save(requestAnnounce);
    }

    public List<RequestAnnounce> findAll() {
        return repo.findAll();
    }
    public RequestAnnounce updateStatus(int id, int status) {
        RequestAnnounce announce = repo.findById(id).orElseThrow(() -> new RuntimeException("Announcement not found"));
        announce.setStatus(RequestAnnounce.Status.values()[status]);  // Update status based on ordinal
        return repo.save(announce);  // Save the updated announcement
    }
    public Announcement createAnnouncementFromRequest(RequestAnnounce requestAnnounce) throws IOException {
        Announcement announcement = new Announcement();
        System.out.println("Creating announcement from request: " + requestAnnounce.getTitle());

        announcement.setTitle(requestAnnounce.getTitle());
        announcement.setContent(requestAnnounce.getContent());
        announcement.setCloudUrl(requestAnnounce.getCloudUrl());
        announcement.setFileExtension(requestAnnounce.getFileExtension());
        announcement.setPublicId(requestAnnounce.getPublicId());
        announcement.setResourceType(requestAnnounce.getResourceType());
        announcement.setScheduledDate(LocalDateTime.now());  // You can set this based on logic
        System.out.println("Saving announcement: " + announcement.getTitle());

        // Save the announcement to the database
        return announcementService.createAnnouncement(announcement, null, false);
    }
}