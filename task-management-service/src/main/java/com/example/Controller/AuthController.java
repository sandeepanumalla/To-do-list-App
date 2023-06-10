package com.example.Controller;

import com.example.config.RestEndpoints;
import com.example.request.UserSignInRequest;
import com.example.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestEndpoints.AUTH)
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(RestEndpoints.SIGN_IN)
    public ResponseEntity<?> signIn(@RequestBody @Valid UserSignInRequest userSignInRequest) {
        authService.signIn(userSignInRequest);
        return ResponseEntity.ok().body("logged in successfully");
    }

    public void signOut() {

    }
}
