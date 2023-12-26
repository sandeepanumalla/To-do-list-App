package com.example.websocket;

import com.example.taskmanagementservice.TaskManagementServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@ContextConfiguration(classes = TaskManagementServiceApplication.class)
public class NotificationServiceTest {

    private final SimpMessagingTemplate simpMessagingTemplate;


    private final WebTestClient webSocketClient;


    @Autowired
    public NotificationServiceTest(SimpMessagingTemplate simpMessagingTemplate, WebTestClient webSocketClient) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.webSocketClient = webSocketClient;
    }

    @Test
    public void sendMessage() {
        simpMessagingTemplate.convertAndSend("/app/send.anything", "I love you");
    }
}
