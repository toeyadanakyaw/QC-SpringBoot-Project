package com.announce.AcknowledgeHub_SpringBoot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class AnnouncementSchedulerService {

    private final TaskScheduler taskScheduler;
    private final Map<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Autowired
    public AnnouncementSchedulerService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void scheduleAnnouncement(int announcementId, Runnable task, LocalDateTime dateTime) {
        cancelScheduledAnnouncement(announcementId);  // Cancel any existing schedule for this post
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
        scheduledTasks.put(announcementId, scheduledTask);
    }

    public void cancelScheduledAnnouncement(int announcementId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.remove(announcementId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
    }
}
