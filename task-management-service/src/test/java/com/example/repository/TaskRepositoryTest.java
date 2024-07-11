package com.example.repository;

import com.example.model.*;
import com.example.taskmanagementservice.TaskManagementServiceApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TaskManagementServiceApplication.class)
public class TaskRepositoryTest {

    final TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryTableRepository categoryTableRepository;

    @Autowired
    public TaskRepositoryTest(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private User user;
    private Task task;
    private CategoryTable category;


    @BeforeEach
    public void setup() {
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

        // Create a dummy category
        category = CategoryTable.builder()
                .categoryName("Test Category")
                .categoryOwner(user)
                .build();

        category = categoryTableRepository.save(category);

        // Create a dummy task
        task = Task.builder()
                .title("Test Task")
                .description("This is a test task")
                .creationDate(LocalDateTime.now())
                .dueDate(LocalDate.now().plusDays(7))
                .category(category)
                .isImportant(false)
                .taskStatus(TaskStatus.PENDING)
                .owner(user)
                .build();

        // Save the task to the database
        task = taskRepository.save(task);
    }

    @AfterEach
    public void cleanup() {
        // Delete the created task
        taskRepository.delete(task);

        // Delete the created category
        categoryTableRepository.delete(category);

        // Delete the created user
        userRepository.delete(user);
    }


    @Test
    void saveTaskTest() {

        Task task = Task.builder()
                .title("cooking")
                .description("I want to cook before 2pm")
                .build();

        Task task1 = taskRepository.save(task);
        Assertions.assertEquals(task.getTitle(), task1.getTitle());
    }

    @Test
    public void testFindByOwnerIdAndTaskStatus() {
        // Given
        long ownerId = user.getUserId();
        List<TaskStatus> taskStatuses = Collections.singletonList(TaskStatus.PENDING);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<Task> tasks = taskRepository.findByOwnerIdAndTaskStatus(ownerId, taskStatuses, pageRequest);

        // Then
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
        // Add more assertions as needed
    }

//    @Test
//    public void testFindByOwnerIdAndCategory() {
//        // Create a list containing the category for the test
//        List<CategoryTable> categories = Collections.singletonList(category);
//
//        // When
//        PageRequest pageRequest = PageRequest.of(0, 10);
//        Page<Task> tasks = taskRepository.findByOwnerIdAndCategory(user.getUserId(), categories, pageRequest);
//
//        // Then
//        assertNotNull(tasks);
//        assertFalse(tasks.isEmpty());
//
//        // Add more assertions as needed
//
//        // Clean up
//        taskRepository.delete(task);
//        categoryRepository.delete(category);
//
//    }


    @Test
    void getTaskTest() {
        Task task = taskRepository.findById(4L).orElse(null);
        System.out.println(task.toString());
        assertNotNull(task);
    }

    @Test
    void shouldThrowDataIntegrityViolationException() {
        Task task = Task.builder()
                .title("test")
                .description("just testing")
//                .category(Category.PERSONAL)
//                .priority(Priority.HIGH)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> taskRepository.save(task));
    }

    @Test
    @Transactional
    void getAllTasksTest() {
//        Optional<User> user = userRepository.findById(1L);
//        Pageable pageable = PageRequest.of(0, 3, Sort.by("title").ascending());
//        Page<Task> listOfTasks = taskRepository.findByOwnerId(user.get(), pageable);
//        System.out.println(listOfTasks);
//        Assertions.assertTrue(listOfTasks.stream().count() > 0);
    }

    @Test
    @Transactional
    void existsSharedTaskForUsersTest() {
        // Create and save some tasks to the database

        List<Long> list = new ArrayList<>();
//        list.add(1L);
        list.add(2L);


        // Create and save a user with shared tasks

        // Call the repository method to check for shared tasks
        boolean ans = taskRepository.existsSharedTaskForUsers(list, 1L);

        // Assert that Task 3 is not in the sharedTasks list
        assertFalse(ans);
    }

    @Test
    void isOwnerTryingToShareHimselfTEst() {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);

        boolean isOwnerTryingToShareHimself = taskRepository.isOwnerTryingToShareToHimself(1L, list);

        Assertions.assertTrue(isOwnerTryingToShareHimself);
    }

    @Test
    @Transactional
    void removeOldUsersFromSharedTaskTest() {
        System.out.println(userRepository.findById(1L).get().getSharedTasks().size());
//       taskRepository.deleteFromSharedTasks(1L, 1L);
    }

    @Test
    void findSharedTasksByUserId() throws Exception {
        User user = userRepository.findById(2L).orElseThrow(() -> new Exception("User not found"));
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "creationDate"));
        Page<Task> tasks = taskRepository.findSharedTasksByUser(user, pageable);
        tasks.forEach(task -> System.out.println(task.getTitle()));
    }
}

