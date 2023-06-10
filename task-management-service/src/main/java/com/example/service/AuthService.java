package com.example.service;

import com.example.request.UserSignInRequest;

public interface AuthService {

    public void signIn(UserSignInRequest userSignInRequest);

    public void signOut();

    public void registerUser();

    public void authenticate();

    public void forgotPassword();

    public void forgotUsername();
}
