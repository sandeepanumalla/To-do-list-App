package com.example.Controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@CrossOrigin(allowCredentials = "true", origins = "{http://localhost:3000,http://localhost:8181}")
public class TestController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public TestController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    @GetMapping("/hello-world/**")
    String helloWorld() {
//        System.out.println(jwtCookie);
        return "hello world";
    }


    @GetMapping("/notification/{message}")
    public String testNotification(@PathVariable String message) {
        simpMessagingTemplate.convertAndSend("/topic/notifications/sandeep", "hello notification" + message);
        return "notification sent";
    }

}
