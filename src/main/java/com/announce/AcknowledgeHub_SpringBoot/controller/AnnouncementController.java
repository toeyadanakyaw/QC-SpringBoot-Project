package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.entity.*;
import com.announce.AcknowledgeHub_SpringBoot.model.AnnouncementDTO;
import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementReadStatusRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.NotificationRepo;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import com.announce.AcknowledgeHub_SpringBoot.service.AnnouncementBotService;
import com.announce.AcknowledgeHub_SpringBoot.service.AnnouncementService;
import com.announce.AcknowledgeHub_SpringBoot.service.StaffService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "http://localhost:4200")
public class AnnouncementController {

    private final ModelMapper mapper;
    private final AnnouncementService announcementService;
    private final StaffService staffService;
    private final UserRepository userRepository;
    private final NotificationRepo notificationRepo;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AnnouncementBotService announcementBotService;
    private final AnnouncementReadStatusRepository announcementReadStatusRepository;

    public AnnouncementController(ModelMapper mapper, AnnouncementService announcementService, StaffService staffService, UserRepository userRepository, NotificationRepo notificationRepo, SimpMessagingTemplate simpMessagingTemplate, AnnouncementBotService announcementBotService, AnnouncementReadStatusRepository announcementReadStatusRepository) {
        this.mapper = mapper;
        this.announcementService = announcementService;
        this.staffService = staffService;
        this.userRepository = userRepository;
        this.notificationRepo = notificationRepo;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.announcementBotService = announcementBotService;
        this.announcementReadStatusRepository = announcementReadStatusRepository;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createAnnouncement(@RequestPart(value = "file") MultipartFile file,
                                                @RequestPart("data") String data) throws MessagingException, IOException, TelegramApiException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        AnnouncementDTO dto = objectMapper.readValue(data, AnnouncementDTO.class);

        Announcement announcementEntity = mapper.map(dto, Announcement.class);
        List<Group> selectedGroups = staffService.getGroupsByIds(dto.getGroupIds());
        List<User> selectedStaff = staffService.getStaffByIds(dto.getStaffIds());
        User user = userRepository.findById(dto.getUser_id()).orElseThrow(() -> new RuntimeException("User not found"));

        announcementEntity.setDocumentName(file.getOriginalFilename());
        announcementEntity.setGroups(selectedGroups);
        List<AnnouncementReadStatus> announcementUsers = selectedStaff.stream()
                .map(temp -> {
                    AnnouncementReadStatus staff = new AnnouncementReadStatus();
                    staff.setAnnouncement(announcementEntity);
                    staff.setStaff(temp);
                    // Set additional columns if needed
                    return staff;
                })
                .toList();
        announcementEntity.setStaffMembers(announcementUsers);
        announcementEntity.setUser(user);

        Announcement createdAnnouncement = announcementService.createAnnouncement(announcementEntity, file, false);
        boolean isSent = announcementService.sendAnnouncement(createdAnnouncement.getId());
        if (!isSent) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Announcement has already been sent.");
        }

        // Filter and notify only after the announcement has been sent
        List<User> recipients = selectedStaff.stream()
                .filter(staff -> staff.getAcceptedAnnouncements().contains(createdAnnouncement))
                .collect(Collectors.toList());

        for (User recipient : recipients) {
            System.out.println("Recipient ID"+recipient);
            Notification notification = new Notification();
            notification.setUser(recipient);
            notification.setAnnouncement(createdAnnouncement);
            notification.setMessage("New announcement: " + createdAnnouncement.getTitle());
            notification.setCreated_at(new Date());

            notificationRepo.save(notification);
            simpMessagingTemplate.convertAndSend("/topic/notifications"+recipient.getId(), notification);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Announcement has been created and sent to targeted recipients");
    }


    @GetMapping
    public ResponseEntity<List<AnnouncementDTO>>getAnnouncements(){
        List<Announcement>announcementEntities=announcementService.getAnnouncements();
        if(!announcementEntities.isEmpty()){
            List<AnnouncementDTO> dtos=announcementEntities.stream()
                    .map(announcements -> mapper.map(announcements, AnnouncementDTO.class))
                    .toList();
            return new ResponseEntity<>(dtos,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("{id}/re-schedule")
    public ResponseEntity<?> reScheduleAnnouncement(@PathVariable int id, @RequestPart(value = "file", required = false) MultipartFile file,
                                                    @RequestPart("data") String data) throws JsonProcessingException{

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        AnnouncementDTO dto = objectMapper.readValue(data, AnnouncementDTO.class);

        boolean isUpdated = announcementService.updateScheduledAnnouncement(id, dto.getScheduledDate());
        if(!isUpdated){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot reschedule an announcement that has already been sent");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("Rescheduled the announcement successfully");
    }

    @PutMapping("{id}/cancel-schedule")
    public ResponseEntity<?> cancelScheduleAnnouncement(@PathVariable int id) {
        boolean isCancelled = announcementService.cancelScheduledAnnouncement(id);
        if(!isCancelled){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot cancel a scheduled announcement that has already been sent");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("Cancelled the schedule for announcement ID " + id + " successfully.");
    }



}
