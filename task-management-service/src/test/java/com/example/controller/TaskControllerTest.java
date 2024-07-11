package com.example.controller;

import com.example.Controller.TaskController;
import com.example.config.RestEndpoints;
import com.example.exceptions.TaskNotFoundException;
import com.example.model.*;
        import com.example.repository.TaskRepository;
import com.example.request.*;
        import com.example.response.ReminderResponse;
import com.example.response.TaskResponse;
import com.example.service.ReminderService;
import com.example.service.TaskSharingService;
import com.example.service.TaskService;
import com.example.service.UserService;
import com.example.service.impl.ReminderServiceImpl;
import com.example.service.impl.TaskServiceImpl;
import com.example.service.impl.TaskSharingServiceImpl;
import com.example.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.*;
        import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TaskControllerTest {

}

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@ContextConfiguration(classes = {TaskController.class,
//        TaskService.class, UserService.class,
//        ModelMapper.class, TaskRepository.class,
//        UserServiceImpl.class, TaskServiceImpl.class,
//        ReminderServiceImpl.class, TaskSharingServiceImpl.class })
//@ActiveProfiles("test")
//public class TaskControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Autowired
//    private TaskController taskController;
//
//    @Autowired
//    private TaskService taskService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ReminderService reminderService;
//
//    @Autowired
//    private TaskSharingService sharedTaskService;
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Autowired
//    private TaskRepository taskRepository;
//
//    @BeforeEach
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
//    }
//
//    @Test
//    public void updateTaskStatusField_validInput_returnsOk() throws Exception {
//        // Arrange
//        TaskStatus status = TaskStatus;
//        Long taskId = 1L;
//        User owner = new User();
//        owner.setUserId(1L);
//        Task task = new Task();
//        task.setId(taskId);
//        task.setTaskStatus(TaskStatus.OPEN);
//        task.setTaskOwner(owner);
//        Reminder reminder = new Reminder();
//        reminder.setId(1L);
//        task.setReminders(List.of(reminder));
//        this.taskService.saveTask(task);
//
//        // Act
//        MockHttpServletRequestBuilder request = patch(RestEndpoints.UPDATE_TASK_STATUS_BY_ID.replace("{taskId}", taskId.toString()))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(status));
//
//        // Assert
//        mockMvc.perform(request)
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void updateTaskStatusField_invalidTaskId_throwsException() throws Exception {
//        // Arrange
//        TaskStatus status = TaskStatus.IN_PROGRESS;
//        Long taskId = 1L;
//        User owner = new User();
//        owner.setUserId(1L);
//        Task task = new Task();
//        task.setId(taskId);
//        task.setTaskStatus(TaskStatus.OPEN);
//        task.setTaskOwner(owner);
//        Reminder reminder = new Reminder();
//        reminder.setId(1L);
//        task.setReminders(List.of(reminder));
//        this.taskService.saveTask(task);
//
//        // Act
//        MockHttpServletRequestBuilder request = patch(RestEndpoints.UPDATE_TASK_STATUS_BY_ID.replace("{taskId}", "2"))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(status));
//
//        // Assert
//        mockMvc.perform(request)
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Task not found"));
//    }
//
//    @Test
//    public void updateTaskStatusField_invalidStatus_throwsException() throws Exception {
//        // Arrange
//        TaskStatus status = TaskStatus.COMPLETED;
//        Long taskId = 1L;
//        User owner = new User();
//        owner.setUserId(1L);
//        Task task = new Task();
//        task.setId(taskId);
//        task.setTaskStatus(TaskStatus.OPEN);
//        task.setTaskOwner(owner);
//        Reminder reminder = new Reminder();
//        reminder.setId(1L);
//        task.setReminders(List.of(reminder));
//        this.taskService.saveTask(task);
//
//        // Act
//        MockHttpServletRequestBuilder request = patch(RestEndpoints.UPDATE_TASK_STATUS_BY_ID.replace("{taskId}", taskId.toString()))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(status));
//
//        // Assert
//        mockMvc.perform(request)
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Invalid status"));
//    }
//
//    @Test
//    public void updateTaskStatusField_unauthorized_throwsException() throws Exception {
//        // Arrange
//        TaskStatus status = TaskStatus.IN_PROGRESS;
//        Long taskId = 1L;
//        User owner = new User();
//        owner.setUserId(1L);
//        Task task = new Task();
//        task.setId(taskId);
//        task.setTaskStatus(TaskStatus.OPEN);
//        task.setTaskOwner(owner);
//        Reminder reminder = new Reminder();
//        reminder.setId(1L);
//        task.setReminders(List.of(reminder));
//        this.taskService.saveTask(task);
//
//        // Act
//        MockHttpServletRequestBuilder request = patch(RestEndpoints.UPDATE_TASK_STATUS_BY_ID.replace("{taskId}", taskId.toString()))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(status));
//
//        // Assert
//        mockMvc.perform(request)
//                .andExpect(status().isForbidden());
//    }
//
//    private String asJsonString(final Object obj) {
//        try {
//            return new ObjectMapper().writeValueAsString(obj);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
