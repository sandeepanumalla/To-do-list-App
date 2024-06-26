package com.example.repository;

import com.example.model.*;
import com.example.taskmanagementservice.TaskManagementServiceApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = TaskManagementServiceApplication.class)
public class TaskRepositoryTest {

    final TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public TaskRepositoryTest(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Test
    void saveTaskTest() {

        Task task = Task.builder()
                .title("cooking")
                .description("I want to cook before 2pm")
//                .category(Category.PERSONAL)
//                .priority(Priority.HIGH)
//                .ownerId(null) // to be changed
                .build();

        Task task1 = taskRepository.save(task);
        Assertions.assertEquals(task.getTitle(), task1.getTitle());
    }

    @Test
    void getTaskTest() {
        Task task = taskRepository.findById(4L).orElse(null);
        System.out.println(task.toString());
        Assertions.assertNotNull(task);
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
        Assertions.assertFalse(ans);
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

