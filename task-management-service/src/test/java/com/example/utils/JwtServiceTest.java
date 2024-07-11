package com.example.utils;

import com.example.model.User;
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
        User user = User.builder()
                .username("sandeep")
                .build();
        String token = jwtService.generateToken(user.getUsername());
        System.out.println("the token is " + token);
        String actualUsername = jwtService.extractUsername(token);
        boolean isTokenValid = jwtService.isValidToken(token, user);
        Assertions.assertEquals(user.getUsername(), actualUsername);
    }

    @Test
    public void testExtractUsername() {
        // TODO: Implement test logic for token extraction of username
    }


    @Test
    public void testExtractExpiration() {
        // TODO: Implement test logic for token extraction of expiration date
    }

    @Test
    public void testIsTokenExpired() {
        // TODO: Implement test logic for token expiration check
    }

    @Test
    public void testIsValidToken_ValidToken() {
        // TODO: Implement test logic for token validity check with a valid token
    }

    @Test
    public void testIsValidToken_ExpiredToken() {
        // TODO: Implement test logic for token validity check with an expired token
    }

    @Test
    public void testIsValidToken_IncorrectUsername() {
        // TODO: Implement test logic for token validity check with an incorrect username
    }

    @Test
    public void testIsValidToken_NullUserDetails() {
        // TODO: Implement test logic for token validity check with null user details
    }


}
