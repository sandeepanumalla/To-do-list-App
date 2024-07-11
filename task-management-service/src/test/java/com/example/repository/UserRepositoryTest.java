package com.example.repository;

import com.example.model.Task;
import com.example.model.User;
import com.example.taskmanagementservice.TaskManagementServiceApplication;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.exceptions.ExceptionIncludingMockitoWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
//@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TaskManagementServiceApplication.class)
public class UserRepositoryTest {

    @Autowired
    private  UserRepository userRepository;


    @Test
    void findByEmail() throws Exception {
        String usernameOrEmail = "sandeep@gmail.com";
//        User user = userRepository.findUserByEmail(usernameOrEmail)
//                .orElseThrow(() -> new Exception("df"));
        assertThrows(Exception.class, () -> userRepository.findUserByEmail(usernameOrEmail)
                .orElseThrow(() -> new Exception("df")));
//        Assertions.assertEquals(user.getEmail(), usernameOrEmail);
    }

//    void findByEmailTest()

    @Test
    void deleteById() {
        Long id = 1L;
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        List<Task> taskList = user.getSharedTasks();

        assertDoesNotThrow(() -> userRepository.deleteById(id));
    }
}
