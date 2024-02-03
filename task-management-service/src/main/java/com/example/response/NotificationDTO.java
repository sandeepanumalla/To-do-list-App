package com.example.response;


import com.example.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String title;
    private String message;
    private NotificationType notificationType;
    private String recipientUsername;
    private String acknowledgementId;
    private boolean read;
    private LocalDateTime createdAt;
}
