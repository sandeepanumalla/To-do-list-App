package com.example.service.factory;

import com.example.model.UserNotification;
import com.example.response.NotificationDTO;

import java.time.LocalDateTime;

public class ReminderNotificationFactory implements NotificationFactory {
    @Override
    public UserNotification createNotifications(String message) {
        return null;
    }

    @Override
    public NotificationDTO createNotificationDTO(String message, String recipientUsername) {
        return NotificationDTO.builder()
                .title("this is a reminder for ")
                .recipientUsername(recipientUsername)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
