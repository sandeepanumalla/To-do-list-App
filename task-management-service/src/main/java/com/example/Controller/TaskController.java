package com.example.Controller;

import com.example.config.RestEndpoints;
import com.example.request.TaskRequest;
import com.example.response.TaskResponse;
import com.example.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RestEndpoints.BASE)
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(RestEndpoints.CREATE_TASK)
    public ResponseEntity<?> createTask(@RequestBody TaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.createTask(taskRequest);
        return ResponseEntity.ok().body(taskResponse);
    }
}
