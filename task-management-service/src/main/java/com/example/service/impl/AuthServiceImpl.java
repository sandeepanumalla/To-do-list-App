package com.example.service.impl;

import com.example.request.UserRegisterRequest;
import com.example.request.UserSignInRequest;
import com.example.service.AuthService;
import com.example.utils.CustomAuthenticationProvider;
import com.example.utils.JwtService;
import com.example.validations.ValidEmailOrUsernameValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final ValidEmailOrUsernameValidator validator;

    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    public AuthServiceImpl(JwtService jwtService, ValidEmailOrUsernameValidator validator, CustomAuthenticationProvider customAuthenticationProvider) {
        this.jwtService = jwtService;
        this.validator = validator;
        this.customAuthenticationProvider = customAuthenticationProvider;

    }

    @Override
    public String signIn(UserSignInRequest userSignInRequest) {
        if(checkAuthentication(userSignInRequest.getEmailOrUsername())) {
            return "you are already signed in";
        }
        String emailOrUsername = userSignInRequest.getEmailOrUsername();
        setAuthentication(emailOrUsername, userSignInRequest.getPassword());
        System.out.println("received the request"+userSignInRequest.toString());
        String token = jwtService.generateToken(emailOrUsername);
        System.out.println("is validated " + validator.toString());

        return token;
    }

    public void setAuthentication(String username, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticated = customAuthenticationProvider.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticated);
    }

    private boolean checkAuthentication(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && authentication.getName().equals(username);
    }

    @Override
    public void signOut() {

    }

    @Override
    public String registerUser(UserRegisterRequest userRegisterRequest) {

        return null;
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
