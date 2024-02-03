package com.example.config;

import com.example.exceptions.JwtAuthenticationException;
import com.example.filters.JwtAuthorizationFilter;

import com.example.request.OAuth2UserRegisterRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {




    private final String[] allowedUrls = {"/api/auth/**", "/test/**",
            "/v3/api-docs/**", "/swagger-ui/**",
            "/sign-in",
            "/oauth2/**",
            "/task-management-sockets/**"
    };

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    private final JwtAuthenticationException jwtAuthenticationException;

    @Autowired
    public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter, JwtAuthenticationException jwtAuthenticationException) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.jwtAuthenticationException = jwtAuthenticationException;
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authorize) -> {
                    authorize
//                            .requestMatchers(allowedUrls).permitAll()
                            .anyRequest().permitAll();
                })
//                .addFilterBefore(oAuth2RedirectionFilter, BasicAuthenticationFilter.class)
                .oauth2Login(auth -> auth.loginPage("/oauth2/authorization/google").defaultSuccessUrl("/home").successHandler(jwtAuthenticationSuccessHandler()))
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.authenticationEntryPoint(jwtAuthenticationException);
                })
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler jwtAuthenticationSuccessHandler() {
        System.out.println("->->->->->");
        return ((request, response, authentication) -> {
//            String jwtToken = generateJwtToken(authentication);
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String firstName = oAuth2User.getAttribute("name");
            String lastName = oAuth2User.getAttribute("family_name");
            if(firstName != null) {
                firstName = firstName.split(" ")[0];
            }
            String requestType = (String) request.getSession().getAttribute("OAuth2_Request_Type");
            String redirectUrl = null;
            if(requestType != null && requestType.equals("register")) {
                redirectUrl = "/oauth2/register/password";
            } else if (requestType != null && requestType.equals("sign-in")) {
                redirectUrl = RestEndpoints.AUTH + "/oauth2/sign-in";
            }

            OAuth2UserRegisterRequest oAuth2UserRegisterRequest = OAuth2UserRegisterRequest.builder()
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();
            HttpSession session = request.getSession();
            session.setAttribute("registrationForm", oAuth2UserRegisterRequest);
            response.sendRedirect(redirectUrl);
        });
    }





    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
