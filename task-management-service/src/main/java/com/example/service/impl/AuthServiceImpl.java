package com.example.service.impl;

import com.example.request.UserSignInRequest;
import com.example.service.AuthService;
import com.example.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;

    @Autowired
    public AuthServiceImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void signIn(UserSignInRequest userSignInRequest) {
        System.out.println("received the request"+userSignInRequest.toString());
    }

    @Override
    public void signOut() {

    }

    @Override
    public void registerUser() {

    }

    @Override
    public void authenticate() {

    }

    @Override
    public void forgotPassword() {

    }

    @Override
    public void forgotUsername() {

    }

}
