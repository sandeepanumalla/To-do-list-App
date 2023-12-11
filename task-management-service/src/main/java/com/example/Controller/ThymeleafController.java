package com.example.Controller;

import com.example.request.OAuth2UserRegisterRequest;
import com.example.request.UserRegisterRequest;
import com.example.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // Use @Controller to define a Spring MVC controller// Specify the base path for this controller
@Slf4j
@RequestMapping("/oauth2")
public class ThymeleafController {

    private final AuthService authService;

    public ThymeleafController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/register/password")
//    @Operation(summary = "Render the registration form for OAuth2", description = "Displays the registration form for OAuth2 users.")

    public String registerFormForOAuth2(HttpServletRequest request, HttpServletResponse response, Model model) {
        log.info("inside registration request.");
        HttpSession session =  request.getSession();
        OAuth2UserRegisterRequest oAuth2UserRegisterRequest = (OAuth2UserRegisterRequest) session.getAttribute("registrationForm");
        model.addAttribute("registrationForm", oAuth2UserRegisterRequest);
        String email = oAuth2UserRegisterRequest.getEmail();
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .username(null)
                .email(email)
                .build();
        try {
            authService.checkUserAvailabilityInDatabase(userRegisterRequest);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        System.out.println(oAuth2UserRegisterRequest);
        return "registration";
    }

        @GetMapping("/register")
        @Operation(summary = "Render the registration form for OAuth2", description = "Displays the registration form for OAuth2 users.")

        public String registerFormForOAuth2(HttpServletRequest request, HttpServletResponse response) {
        return "redirect:/oauth2/authorization/google";
    }
}
