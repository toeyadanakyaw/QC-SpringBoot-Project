package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public DashboardService(AnnouncementRepository announcementRepository, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
    }

    public int getAnnouncementCount() {
        return announcementRepository.countAllAnnouncements();
    }

    public int getUserCount() {
        return userRepository.countAllUsers();
    }

    public int getUpcomingAnnouncementCount() {
        return announcementRepository.countUpcomingAnnouncements();
    }

    public Map<String, Object> getTotalAnnouncementsData(String view) {
        Map<String, Object> data = new HashMap<>();
        List<Object[]> results;

        if ("monthly".equals(view)) {
            results = announcementRepository.countTotalAnnouncementsByMonth();
        } else {
            results = announcementRepository.countTotalAnnouncementsByYear();
        }

        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (Object[] result : results) {
            labels.add(result[0].toString()); // Date or Month/Year
            values.add(((Number) result[1]).intValue()); // Count
        }

        data.put("labels", labels);
        data.put("values", values);
        return data;
    }

    public Map<String, Object> getAnnouncementsByDepartmentData() {
        Map<String, Object> data = new HashMap<>();
        List<Object[]> results = announcementRepository.countAnnouncementsByDepartment();

        List<String> departments = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        for (Object[] result : results) {
            departments.add(result[0].toString()); // Department Name
            counts.add(((Number) result[1]).intValue()); // Count
        }

        data.put("departments", departments);
        data.put("counts", counts);
        return data;
    }


}