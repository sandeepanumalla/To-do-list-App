//package com.example.service.impl;
//
//import com.example.model.NotificationType;
//import com.example.model.User;
//import com.example.response.NotificationDTO;
//import com.example.service.UserNotificationService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@Slf4j
//public class UserNotificationServiceImpl implements UserNotificationService {
//
//
//    private final SimpMessagingTemplate simpMessagingTemplate;
//
//    private final RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    public UserNotificationServiceImpl(SimpMessagingTemplate simpMessagingTemplate, RabbitTemplate rabbitTemplate) {
//        this.simpMessagingTemplate = simpMessagingTemplate;
//        this.rabbitTemplate = rabbitTemplate;
//    }
//
//    private void sendNotification(NotificationDTO notificationDTO) {
//        String recipientUsername = notificationDTO.getRecipientUsername();
//        String acknowledgementId = UUID.randomUUID().toString();
//        notificationDTO.setAcknowledgementId(acknowledgementId);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json;
//        try {
//             json = objectMapper.writeValueAsString(notificationDTO);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
////        rabbitTemplate.convertAndSend("notification-exchange", "user.".concat(recipientUsername), notificationDTO);
//        simpMessagingTemplate.convertAndSend("/topic/notifications/" + recipientUsername,  json);
////        simpMessagingTemplate.convertAndSendToUser(recipientUsername, "/topic/notifications", json);
//        log.info(String.format("notification sent to %s", recipientUsername));
//    }
//
//    @Override
//    public List<NotificationDTO> generateNotifications(List<NotificationDTO> usersToBeNotified) {
//        for(NotificationDTO notificationDTO: usersToBeNotified) {
//            sendNotification(notificationDTO);
//        }
//        return usersToBeNotified;
//    }
//
//
//    @RabbitListener(queues = "user-queue")
//    public void receiveNotification(NotificationDTO notificationDTO) {
//        String recipientUsername = notificationDTO.getRecipientUsername();
//        String topic = "/topic/" + recipientUsername + "/notifications";
//        simpMessagingTemplate.convertAndSend(topic, notificationDTO);
//    }
//
//
//    @Override
//    public List<User> getSubscribedMembers(NotificationType notificationType) {
//        return null;
//    }
//}
