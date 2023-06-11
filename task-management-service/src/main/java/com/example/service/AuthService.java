package com.example.service;

import com.example.request.UserRegisterRequest;
import com.example.request.UserSignInRequest;

public interface AuthService {

    public String signIn(UserSignInRequest userSignInRequest);

    public void signOut();

    public String registerUser(UserRegisterRequest userRegisterRequest);

    public void authenticate();

    public void forgotPassword();

    public void forgotUsername();
}
