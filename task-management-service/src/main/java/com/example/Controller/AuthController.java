package com.example.Controller;

import com.example.config.RestEndpoints;
import com.example.model.AuthenticationType;
import com.example.request.OAuth2UserRegisterRequest;
import com.example.request.UserRegisterRequest;
import com.example.request.UserSignInRequest;
import com.example.response.UserWelcomeResponse;
import com.example.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.sql.SQLException;
import java.util.Arrays;

@RestController
@RequestMapping(RestEndpoints.AUTH)
@Slf4j
public class AuthController {

    private final AuthService authService;

    private final ModelMapper mapper;

    @Autowired
    public AuthController(AuthService authService, ModelMapper mapper) {
        this.authService = authService;
        this.mapper = mapper;
    }

    @PostMapping(RestEndpoints.REGISTER)
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) throws SQLException {
        String token = authService.registerUser(userRegisterRequest);
        return ResponseEntity.ok().body("you are registered successfully");
    }

    @PostMapping(RestEndpoints.SIGN_IN)
    public ResponseEntity<?> signIn(@RequestBody @Valid UserSignInRequest userSignInRequest, HttpServletRequest request, HttpServletResponse response) {
        UserWelcomeResponse userWelcomeResponse = null;
        return signInResponseEntity(response, userSignInRequest);
    }

    private void setCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge, String path, boolean httpOnly, boolean secure) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        response.addCookie(cookie);
    }

    @GetMapping(RestEndpoints.FORGOT_PASSWORD)
    public ResponseEntity<?> forgotPassword(@RequestBody String email){
        authService.forgotPassword(email);
        return ResponseEntity.ok().body("reset password link has been sent to your email");
    }

    @GetMapping(RestEndpoints.SIGN_OUT)
    public ResponseEntity<?> signOut( HttpServletRequest request, HttpServletResponse response) {

        Cookie httpOnlyCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("jwt"))
                .findFirst()
                .orElse(null);

        if(httpOnlyCookie != null) {
            Cookie deletedCookie = new Cookie("jwt", "");
            deletedCookie.setMaxAge(0);
            deletedCookie.setPath("/");
            response.addCookie(deletedCookie);
            return ResponseEntity.ok().body("you're logged out.");
        }

        return  ResponseEntity.ok().body("you're already logged out.");
    }

    @GetMapping(RestEndpoints.RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok().body("reset password link has been sent to your email");
    }

    @PostMapping("/oauth2/register/confirm-password")
    public ResponseEntity<?> OAuth2Register(@ModelAttribute OAuth2UserRegisterRequest registerRequest,
                                            HttpServletRequest req,
                                            HttpServletResponse res
    ){
        log.debug("inside post call");
        log.info(String.valueOf(registerRequest));
        if (registerRequest != null) {
            UserRegisterRequest userRegisterRequest = mapper.map(registerRequest, UserRegisterRequest.class);
            try {
                authService.registerUser(userRegisterRequest);
            } catch (SQLException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while registering the user");
            }
            log.debug(userRegisterRequest.toString());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid registration request");
        }
        return ResponseEntity.ok().body("user has been registered");
    }


    @GetMapping("/oauth2/sign-in")
    @Operation(hidden = true)
    public ResponseEntity<String> Oauth2SignIn(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session =  request.getSession();
        OAuth2UserRegisterRequest oAuth2UserRegisterRequest = (OAuth2UserRegisterRequest) session.getAttribute("registrationForm");
        UserSignInRequest userSignInRequest = mapper.map(oAuth2UserRegisterRequest, UserSignInRequest.class);
        userSignInRequest.setEmailOrUsername(oAuth2UserRegisterRequest.getEmail());
        userSignInRequest.setAuthenticationType(AuthenticationType.OAUTH2);
        UserWelcomeResponse userWelcomeResponse;
        return signInResponseEntity(response, userSignInRequest);
    }

    private ResponseEntity<String> signInResponseEntity(HttpServletResponse response, UserSignInRequest userSignInRequest) {
        UserWelcomeResponse userWelcomeResponse;
        try {
            userWelcomeResponse = authService.signIn(userSignInRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setCookie(response, "jwt", userWelcomeResponse.getToken(), 86400, "/", true, false);
        String message = String.format("Hey %s, welcome back!", userWelcomeResponse.getFirstName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(message);
    }


}
