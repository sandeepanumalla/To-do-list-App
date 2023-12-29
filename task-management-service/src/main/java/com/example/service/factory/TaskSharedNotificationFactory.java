package com.example.service.factory;

import com.example.model.UserNotification;
import com.example.response.NotificationDTO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TaskSharedNotificationFactory implements NotificationFactory {
    @Override
    public UserNotification createNotifications(String message) {
        return null;
    }

    @Override
    public NotificationDTO createNotificationDTO(String message, String recipientUsername) {

        return NotificationDTO.builder()
                .title("task has been shared")
                .recipientUsername(recipientUsername)
                .message(message)
                .build();
    }
}
