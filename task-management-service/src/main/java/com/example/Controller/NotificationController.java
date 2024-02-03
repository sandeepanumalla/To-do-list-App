package com.example.Controller;

import com.example.response.NotificationDTO;
import com.example.service.ReminderService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    private final ReminderService reminderService;

    public NotificationController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @MessageMapping("/topic/notifications/{subtopic}")
    @SendTo("/topic/notifications")
    public NotificationDTO sendNotification(String string, @DestinationVariable String subtopic) {
        System.out.println("This is a notification message." + string);
        System.out.println("Message received on /topic/notifications/" + subtopic + ": " + subtopic);
        return null;
    }

    @MessageMapping("/topic/reminders/{subtopic}")
    @SendTo("/topic/reminders")
    public void sendReminders(String string,  @DestinationVariable String subtopic) {
        System.out.println("This is a reminder message in websockets controller." + string);
        System.out.println("Message received on /topic/reminders/" + subtopic + ": " + subtopic);
//        reminderService.sendReminders();
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
