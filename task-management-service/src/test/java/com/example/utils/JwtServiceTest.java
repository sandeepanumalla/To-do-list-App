package com.example.utils;

import com.example.taskmanagementservice.TaskManagementServiceApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = TaskManagementServiceApplication.class)
public class JwtServiceTest {

    private final JwtService jwtService;

    @Autowired
    public JwtServiceTest(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Test
    public void generateTokenTest() {
        String username  = "sandeep";
        String token = jwtService.generateToken(username);
        System.out.println("the token is " + token);
        String actualUsername = jwtService.extractUsername(token);
        Assertions.assertEquals(username, actualUsername);
    }
}
