package com.example.service.impl.files;

import com.example.utils.JwtService;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final JwtService jwtService;

    public RefreshTokenService(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    public String renewToken(String oldToken) {
        if(!tokenIsValid(oldToken)) {
            throw new IllegalStateException("");
        }
        String username = jwtService.extractUsername(oldToken);
        return jwtService.generateToken(username);
    }

    public boolean tokenIsValid(String token) {
        return jwtService.isTokenExpired(token);
    }

}
