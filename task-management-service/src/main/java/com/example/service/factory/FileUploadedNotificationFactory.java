package com.example.service.factory;

import com.example.model.UserNotification;
import com.example.response.NotificationDTO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FileUploadedNotificationFactory implements NotificationFactory{

    Map<NotificationType, String> titles = Map.of(
            NotificationType.TASK_SHARED, "task has been shared",
            NotificationType.TASK_UNSHARED, "task has been unshared",
            NotificationType.FILE_UPLOAD, "file has been uploaded",
            NotificationType.COMMENT, "someone commented"
    );
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
