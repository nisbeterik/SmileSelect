package com.smile_select.notification_service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smile_select.notification_service.model.Notification;
import com.smile_select.notification_service.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // This will likely never be used, as notifications are not handeled through REST, but is implemented for testing purposes
    @PostMapping
    public ResponseEntity<?> createNewNotification(@RequestBody Notification notification){
        Notification createdNotification = notificationService.save(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendCustomEmail(@RequestBody Map<String, Object> requestBody) {
        String to = (String) requestBody.get("to");
        String subject = (String) requestBody.get("subject");
        String content = (String) requestBody.get("content");

        notificationService.sendEmail(to, subject, content);

        return ResponseEntity.ok("Email sent successfully");
    }
}
