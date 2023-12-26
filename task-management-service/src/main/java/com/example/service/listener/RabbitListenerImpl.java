package com.example.service.listener;

import com.example.response.NotificationDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@Service
public class RabbitListenerImpl {
    Logger logger = LoggerFactory.getLogger(RabbitListenerImpl.class);

    private final SimpMessagingTemplate messageTemplate;
    private final ObjectMapper objectMapper;

    public RabbitListenerImpl(
//            @Qualifier("messagingTemplate")
            SimpMessagingTemplate messageTemplate, ObjectMapper objectMapper) {
        this.messageTemplate = messageTemplate;
        this.objectMapper = objectMapper;
    }


    @RabbitListener(queues = {"${rabbitmq.queue.name}"}, autoStartup = "true")
    public void receiveMessage(String message) {
        logger.info(String.format("Received message at %s", System.currentTimeMillis()));
        logger.info(String.format("Received message from RabbitMQ consumer: %s", message));
        NotificationDTO notificationDTO = null;
        try {
            notificationDTO = objectMapper.readValue(message, NotificationDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String dynamicTopic = "/topic/notifications/" + notificationDTO.getRecipientUsername();
        try {
            messageTemplate.convertAndSend(dynamicTopic, message);
            logger.info(String.format("message sent to ws consumer " + dynamicTopic));
        } catch (Exception e) {
            logger.error("Error sending message: " + e.getMessage(), e);
        }

        // send or public the message to websocket
//        messageTemplate.convertAndSend("/topic/notifications/" + notificationDTO.getRecipientUsername());
    }
}
