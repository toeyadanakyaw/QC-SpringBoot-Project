package com.announce.AcknowledgeHub_SpringBoot.controller;

import com.announce.AcknowledgeHub_SpringBoot.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/counts")
    public Map<String, Integer> getCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("announcementCount", dashboardService.getAnnouncementCount());
        counts.put("userCount", dashboardService.getUserCount());
        counts.put("upcomingAnnouncementCount", dashboardService.getUpcomingAnnouncementCount());
        System.out.println("Counts: " + counts);
        return counts;
    }

    @GetMapping("/total-announcements")
    public Map<String, Object> getTotalAnnouncementsData(@RequestParam String view) {
        return dashboardService.getTotalAnnouncementsData(view);
    }

    @GetMapping("/announcements-by-department")
    public Map<String, Object> getAnnouncementsByDepartmentData() {
        return dashboardService.getAnnouncementsByDepartmentData();
    }

}

