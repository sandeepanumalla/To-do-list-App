package com.example.Controller;

import com.example.response.NotificationDTO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @MessageMapping("/topic/notifications/{subtopic}")
    @SendTo("/topic/notifications")
    public NotificationDTO sendNotification(String string, @DestinationVariable String subtopic) {
        System.out.println("This is a notification message." + string);
        System.out.println("Message received on /topic/notifications/" + subtopic + ": " + subtopic);
        return null;
    }

    @MessageMapping("/send.anything")
    @SendTo("/topic/public")
    public String hello(String string) {
        System.out.println("received! " + string);
        return string;
    }

    @SubscribeMapping("/topic/notifications")
    public String onSubscribe() {
        return "Subscribed to /topic/notifications successfully!";
    }
}
