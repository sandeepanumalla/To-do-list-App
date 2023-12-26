package com.example.service;

import com.example.model.User;
import com.example.request.UserRegisterRequest;
import com.example.request.UserSignInRequest;
import com.example.response.UserWelcomeResponse;
import org.springframework.web.util.UriComponents;

import java.sql.SQLException;

public interface AuthService {

    public UserWelcomeResponse signIn(UserSignInRequest userSignInRequest) throws Exception;

    public void signOut(String token);

    public String registerUser(UserRegisterRequest userRegisterRequest) throws SQLException;

    void checkUserAvailabilityInDatabase(UserRegisterRequest userRegisterRequest) throws IllegalArgumentException;

    public void authenticate();

    public void forgotPassword(String email);

    public void forgotUsername();

    public UriComponents generateResetUrl();
}
