package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.entity.Announcement;
import com.announce.AcknowledgeHub_SpringBoot.entity.AnnouncementReadStatus;
import com.announce.AcknowledgeHub_SpringBoot.entity.Group;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementReadStatusRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {

    private final AnnouncementBotService announcementBotService;
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementSchedulerService announcementSchedulerService;
    private final AnnouncementReadStatusRepository announcementReadStatusRepository;
    private final EmailService emailService;
    private final StaffService staffService;
    private final Cloudinary cloudinary;
    private final String folderName = "YNWA";
    private final AnnouncementReadStatusRepository announcementReadStatusRepository;

    @Autowired
    public AnnouncementService(
            AnnouncementBotService announcementBotService,
            AnnouncementRepository announcementRepository,
            AnnouncementSchedulerService announcementSchedulerService, AnnouncementReadStatusRepository announcementReadStatusRepository,
            EmailService emailService,
            StaffService staffService,
            Cloudinary cloudinary, AnnouncementReadStatusRepository announcementReadStatusRepository
    ) {
        this.announcementBotService = announcementBotService;
        this.announcementRepository = announcementRepository;
        this.announcementSchedulerService = announcementSchedulerService;
        this.announcementReadStatusRepository = announcementReadStatusRepository;
        this.emailService = emailService;
        this.staffService = staffService;
        this.cloudinary = cloudinary;
        this.announcementReadStatusRepository = announcementReadStatusRepository;
    }


    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcements = announcementRepository.findAll();
        return announcements.stream().map(announcement -> {
            // Get the total number of users the announcement was sent to
            int totalRecipients = announcementReadStatusRepository.countByAnnouncement(announcement);

            // Get the number of users who have read the announcement
            int readCount = announcementReadStatusRepository.countByAnnouncementAndIsReadTrue(announcement);

            // Calculate the read progress percentage
            double readProgress = totalRecipients > 0 ? (readCount * 100.0 / totalRecipients) : 0.0;

            // Set the read progress on the Announcement entity
            announcement.setReadProgress(readProgress);

            return announcement;
        }).collect(Collectors.toList());
    }

    public Announcement createAnnouncement(Announcement announcement, MultipartFile file, boolean overwrite) throws IOException {
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
        announcement.setPublicId(publicId);
        announcement.setResourceType(resourceType);
        announcement.setFileExtension(fileExtension); // Store file extension in the announcement

        String cloudUrl = (String) uploadResult.get("secure_url");
        announcement.setCloudUrl(cloudUrl);

        // Log upload details
        System.out.println("File uploaded to Cloudinary. Public ID: " + publicId + ", URL: " + cloudUrl);

        return announcementRepository.save(announcement);
    }

    public boolean sendAnnouncement(Integer announcementId) throws MessagingException, IOException, TelegramApiException {
        Announcement announcement = announcementRepository.findById(Long.valueOf(announcementId))
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        if (announcement.getSent() == 1) {
            return false;
        }

        if (announcement.getScheduledDate() != null) {
            announcementSchedulerService.scheduleAnnouncement(
                    announcement.getId(),
                    () -> {
                        try {
                            sendAnnouncementImmediately(announcement.getId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    announcement.getScheduledDate()
            );
        } else {
            sendAnnouncementImmediately(announcement.getId());
        }

        return true;
    }


    @Transactional
    protected void sendAnnouncementImmediately(Integer announcementId) throws MessagingException, IOException, TelegramApiException {
        Announcement announcement = announcementRepository.findById(Long.valueOf(announcementId))
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        // Add a check here to avoid sending twice
        if (announcement.getSent() == 1) {
            return; // Exit if already sent
        }

        String cloudUrl = announcement.getCloudUrl();
        if (cloudUrl == null || cloudUrl.isEmpty()) {
            throw new RuntimeException("Cloudinary URL is missing for the announcement");
        }

        byte[] fileBytes = downloadFromUrl(cloudUrl);
        if (fileBytes == null || fileBytes.length == 0) {
            throw new RuntimeException("File content is missing from Cloudinary.");
        }

        String fileExtension = announcement.getFileExtension(); // Use stored file extension
        String fileName = extractFileNameFromPublicId(announcement.getPublicId()) + fileExtension;

        Set<String> emailAddresses = new HashSet<>();
        Set<Long> telegramUserIds = new HashSet<>();

        for (Group group : announcement.getGroups()) {
            emailAddresses.addAll(staffService.getEmailsByGroupId(group.getId()));
            telegramUserIds.addAll(staffService.getTelegramUserIdByGroupId(group.getId()));
        }

        for (AnnouncementReadStatus user : announcement.getStaffMembers()) {
            User staff = user.getStaff();
            emailAddresses.add(staff.getEmail());
            telegramUserIds.add(staff.getTelegram_user_id());
        }

        if (announcement.getSent() == 1) {
            throw new IllegalStateException("Announcement has already been sent.");
        } else {
            for (String email : emailAddresses) {
                emailService.sendEmailWithAttachment(email, announcement.getTitle(), announcement.getContent(), fileBytes, fileName);
            }
            for (Long tuid : telegramUserIds) {
                int messageId = announcementBotService.sendAnnouncementWithPDF(tuid, announcement, fileBytes);
                // Update AnnouncementReadStatus with messageId
                AnnouncementReadStatus readStatus = announcement.getStaffMembers().stream()
                        .filter(status -> status.getStaff().getTelegram_user_id().equals(tuid))
                        .findFirst()
                        .orElse(null);
                if (readStatus != null) {
                    readStatus.setMessageId(messageId);
                    readStatus.setIsRead(false);
                    announcementReadStatusRepository.save(readStatus);
                }
            }
            announcement.setSent(1);
            announcementRepository.save(announcement);
        }
    }

    public boolean updateScheduledAnnouncement(int announcementId, LocalDateTime newScheduleDate) {
        Announcement announcement = announcementRepository.findById((long) announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        if (announcement.getSent() == 1) {
            return false;
        }

        announcement.setScheduledDate(newScheduleDate);
        announcementRepository.save(announcement);

        announcementSchedulerService.scheduleAnnouncement(announcementId, () -> {
            try {
                sendAnnouncement(announcementId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, newScheduleDate);

        return true;
    }

    public boolean cancelScheduledAnnouncement(int announcementId) {
        Announcement announcement = announcementRepository.findById((long) announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        if (announcement.getSent() == 1) {
            return false;
        } else {
            announcementSchedulerService.cancelScheduledAnnouncement(announcementId);
            announcement.setScheduledDate(null);
            announcementRepository.save(announcement);
            return true;
        }
    }

    public List<Announcement> getAnnouncements() {
        return announcementRepository.findAll();
    }

    public Map<String, Object> getFileDetails(String publicId) {
        try {
            Announcement announcement = announcementRepository.findByPublicId(publicId)
                    .orElseThrow(() -> new RuntimeException("Announcement not found"));
            return ObjectUtils.asMap(
                    "public_id", announcement.getPublicId(),
                    "resource_type", announcement.getResourceType(),
                    "url", announcement.getCloudUrl(),
                    "name", extractFileNameFromPublicId(announcement.getPublicId()) + announcement.getFileExtension()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file details from Cloudinary", e);
        }
    }

    private String getFileExtension(String filename) {
        String fileExtension = "";
        if (filename != null && filename.lastIndexOf('.') > 0) {
            fileExtension = filename.substring(filename.lastIndexOf('.'));
        }
        return fileExtension;
    }

    private String extractFileNameFromPublicId(String publicId) {
        return publicId.contains("/") ? publicId.substring(publicId.lastIndexOf('/') + 1) : publicId;
    }

    private byte[] downloadFromUrl(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(15000);  // Set connection timeout to 15 seconds
        connection.setReadTimeout(15000);  // Set read timeout to 15 seconds
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download file: HTTP response code " + responseCode);
        }

        try (InputStream in = connection.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }

    }

    public Map<String, Object> getAnnouncementDetails(int announcementId) {
        Optional<Announcement> announcement = announcementRepository.findById((long) announcementId);
        if (announcement.isEmpty()) {
            return null; // Handle the case where the announcement is not found
        }

        List<AnnouncementReadStatus> readStatuses = announcementReadStatusRepository.findByAnnouncementId(announcementId);

        Map<String, Object> result = new HashMap<>();
        result.put("announcement", announcement.get());
        result.put("readStatuses", readStatuses);

        return result;
    }
    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id).orElse(null);
    }
    public byte[] downloadFile(String cloudUrl) throws IOException {
        URL url = new URL(cloudUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }
}
