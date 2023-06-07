package com.example.service;

import com.example.model.Category;
import com.example.model.Priority;
import com.example.repository.TaskRepository;
import com.example.request.TaskRequest;
import com.example.response.TaskResponse;
import com.example.taskmanagementservice.TaskManagementServiceApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = TaskManagementServiceApplication.class)
public class TaskServiceTest {

    private final TaskService taskService;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceTest(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }


    @Test
    public void createTaskTest() {
        TaskRequest taskRequest = TaskRequest
                .builder()
                .title("testing the service")
                .description("I am testing the task service")
                .category(Category.WORK)
                .priority(Priority.MEDIUM)
                .userId(1L)
                .build();

        TaskResponse taskResponse = taskService.createTask(taskRequest);
        Assertions.assertEquals(taskResponse.getTitle(),taskRequest.getTitle());
    }

}
