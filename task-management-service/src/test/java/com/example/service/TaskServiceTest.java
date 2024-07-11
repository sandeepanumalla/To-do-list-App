package com.example.service;

import com.example.model.*;
import com.example.repository.CategoryTableRepository;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import com.example.request.TaskRequest;
import com.example.request.TaskUpdateRequest;
import com.example.response.TaskResponse;
import com.example.taskmanagementservice.TaskManagementServiceApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TaskManagementServiceApplication.class)
public class TaskServiceTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;


    private User user;
    private Task task;
    private CategoryTable category;
    @Autowired
    private CategoryTableRepository categoryTableRepository;

    @BeforeEach
    public void setup() {

        if(userRepository.findUserByEmail("test@example.com").isEmpty()) {
            // Create a dummy user

            // Create a dummy user
            user = User.builder()
                    .username("testUser")
                    .email("test@example.com")
                    .firstName("Test")
                    .lastName("User")
                    .password("password")
                    .build();

            // Save the user to the database
            user = userRepository.save(user);
        } else {
            this.user = userRepository.findUserByEmail("test@example.com").get();
        }

        // Create a dummy category
        category = CategoryTable.builder()
                .categoryName("Test Category")
                .categoryOwner(user)
                .build();

        category = categoryTableRepository.save(category);
//
//        // Create a dummy task
//        task = Task.builder()
//                .title("Test Task")
//                .description("This is a test task")
//                .creationDate(LocalDateTime.now())
//                .dueDate(LocalDate.now().plusDays(7))
//                .category(category)
//                .isImportant(false)
//                .taskStatus(TaskStatus.PENDING)
//                .owner(user)
//                .build();
//
//        // Save the task to the database
//        task = taskRepository.save(task);


    }



    @Test
    public void createTaskTest() {
        Optional<User> userFromDB = userRepository.findUserByEmail(user.getEmail());
        if (userFromDB.isPresent()) {
            user = userFromDB.get();
        } else {
            // Handle the case where user is not found
            throw new RuntimeException("User not found with email");
        }
        LocalDateTime dueDate = LocalDateTime.now().withHour(18).withMinute(0).withSecond(0);
        TaskRequest taskRequest = TaskRequest
                .builder()
                .title("OOPs topic")
                .description("do a OOPS revision 6pm")
                .categoryName("Test Category")
//                .(Priority.MEDIUM)
                .dueDate(dueDate.toLocalDate())
//                .ownerId(userFromDB.get())
                .build();

        TaskResponse taskResponse = null;
        try {
            taskResponse = taskService.createTask( userFromDB.get(),taskRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(taskResponse.getTitle(),taskRequest.getTitle());
    }


    @Test
    public void deleteTaskByTaskIdTest() {
        long taskId = 13L;
        List<Task> listOfTasks = taskRepository.findAll();
        long sizeBeforeDelete = listOfTasks.size();
//        taskService.deleteTask(taskId);
        Assertions.assertEquals(sizeBeforeDelete - 1, taskRepository.findAll().size());
        Assertions.assertTrue(taskRepository.findAll().stream().filter(e -> e.getId() == taskId).toList().size() == 0);
    }

    @Test
    public void updateTaskByTaskIdTest() {
        long taskId = 53L;
        TaskUpdateRequest request = TaskUpdateRequest.builder()
                .id(taskId)
                .title("update service test")
                .description("I am testing test for update service")
                .categoryName("")
                .priority(Priority.HIGH)
                .userId(1L)
                .build();
        taskService.updateTask(request);

        Task task = taskRepository.findById(taskId).orElse(null);
        Assertions.assertEquals(task.getTitle(), request.getTitle());
    }

    public void updateTaskByTaskIdTestShouldThrowIfNotPresent() {
        long taskId = 53L;
        Task task = taskRepository.findById(taskId).orElse(null);
    }


    @Test
    public void TestAddTaskToMyDay() throws Exception {
        User user = userRepository.findById(1L).orElseThrow(() -> new Exception("user not found"));
        Long taskId = 1L;
        taskService.addTaskToMyDay(taskId, user);
    }

    @Test
    public void TestRemoveFromMyDay() throws Exception {
        User user = userRepository.findById(1L).orElseThrow(() -> new Exception("user not found"));
        Long taskId = 1L;
        taskService.removeTaskFromMyDay(taskId, user);
    }

}
