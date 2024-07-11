package com.example.service.impl;

import com.example.response.NotificationDTO;
import com.example.service.MessageBroker;
import com.example.service.listener.RabbitListenerImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RabbitMQMessageBroker implements MessageBroker {

    Logger logger = LoggerFactory.getLogger(RabbitMQMessageBroker.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.routing.analytics.key}")
    private String analyticsRoutingKey;

    private final ObjectMapper objectMapper;

    private final SimpMessagingTemplate messageTemplate;
    public RabbitMQMessageBroker(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, SimpMessagingTemplate messageTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public void sendNotification(NotificationDTO notificationDto, String notificationType) {
//        String ;
        try {
//             json = objectMapper.writeValueAsString(notificationDto);
//            NotificationDTO notificationDTO ;
//             notificationType;
            //                notificationDTO = objectMapper.readValue(json, NotificationDTO.class);
//            String notificationType = notificationDto.getNotificationType().toString();
//            String json = objectMapper.writeValueAsString(notificationDto);
//            String dynamicTopic = "/topic/" +notificationType +"/" + notificationDto.getRecipientUsername();
//            try {
//                messageTemplate.convertAndSend(dynamicTopic, json);
//                logger.info(String.format("message sent to ws consumer " + dynamicTopic));
//            } catch (Exception e) {
//                logger.error("Error sending message: " + e.getMessage(), e);
//            }
             rabbitTemplate.convertAndSend(exchangeName, routingKey, notificationDto);
        } catch ( AmqpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMail() {
        // this will emit the email metadata
        // and rabbitListener will send it
    }

    @Override
    public void send() {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, "receive");
        } catch (AmqpException e) {
            throw new RuntimeException(e);
        }
    }

}
