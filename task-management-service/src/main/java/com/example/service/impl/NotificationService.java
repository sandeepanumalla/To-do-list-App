package com.example.service.impl;

import com.example.response.NotificationDTO;
import com.example.service.MessageBroker;
import com.example.service.factory.NotificationFactory;
import com.example.service.factory.NotificationFactoryProvider;
import com.example.service.factory.NotificationType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class NotificationService{
    private final MessageBroker messageBroker;



    public NotificationService(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }

    public void sendNotification(String recipientName, String message, NotificationType NotificationType) {
        NotificationFactory notificationFactory = NotificationFactoryProvider.getFactory(NotificationType);
        NotificationDTO notificationDTO =  notificationFactory.createNotificationDTO(NotificationType ,message, recipientName);
        messageBroker.sendNotification(notificationDTO, recipientName);
    }
}
