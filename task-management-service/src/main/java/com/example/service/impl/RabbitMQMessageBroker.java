package com.example.service.impl;

import com.example.response.NotificationDTO;
import com.example.service.MessageBroker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class RabbitMQMessageBroker implements MessageBroker {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final ObjectMapper objectMapper;

    public RabbitMQMessageBroker(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendNotification(NotificationDTO notificationDto, String notificationType) {
        String json;
        try {
             json = objectMapper.writeValueAsString(notificationDto);
             rabbitTemplate.convertAndSend(exchangeName, routingKey, json);
        } catch (JsonProcessingException | AmqpException e) {
            throw new RuntimeException(e);
        }
    }

}
