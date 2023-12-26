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
    public NotificationDTO createNotificationDTO(NotificationType notificationType ,String message, String recipientUsername) {
        Map<NotificationType, String> titles = Map.of(
                NotificationType.TASK_SHARED, "task has been shared",
                NotificationType.TASK_UNSHARED, "task has been unshared",
                NotificationType.FILE_UPLOAD, "file has been uploaded",
                NotificationType.COMMENT, "someone commented"
        );
        return NotificationDTO.builder()
                .title(titles.get(notificationType))
                .recipientUsername(recipientUsername)
                .message(message)
                .build();
    }
}
