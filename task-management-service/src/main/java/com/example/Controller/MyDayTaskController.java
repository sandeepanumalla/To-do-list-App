package com.example.Controller;

import com.example.model.User;
import com.example.response.TaskResponse;
import com.example.service.TaskService;
import com.example.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MyDayTaskController {

    private final UserServiceImpl userService;
    private final TaskService taskService;

    public MyDayTaskController(UserServiceImpl userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @RequestMapping(path = "/my-day", method = RequestMethod.GET)
    public ResponseEntity<?> getMyDayTasks(HttpServletRequest request) {
        User user = userService.getUserIdByToken(request);
        List<TaskResponse> taskResponses = taskService.fetchMyDayTasks(user);
        return ResponseEntity.ok().body(taskResponses);
    }
}
