package com.example.service;

import com.example.model.AuthenticationType;
import com.example.repository.UserRepository;
import com.example.request.UserSignInRequest;
import com.example.response.UserWelcomeResponse;
import com.example.service.impl.AuthServiceImpl;
import com.example.taskmanagementservice.TaskManagementServiceApplication;
import com.example.utils.CustomAuthenticationProvider;
import com.example.utils.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TaskManagementServiceApplication.class)
public class AuthServiceTest {


    @Mock
    private MailService mailService;
    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomAuthenticationProvider customAuthenticationProvider;

//    @Mock


    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserSignInRequest userSignInRequest;


    @BeforeEach
    public void setUp() {
        userSignInRequest = new UserSignInRequest();
        userSignInRequest.setEmailOrUsername("valid_email");
        userSignInRequest.setPassword("valid_password");
        userSignInRequest.setAuthenticationType(AuthenticationType.NORMAL);
    }


    @Test
    void generateResetUrlTest() {
        System.out.println(authService.generateResetUrl());
    }

    @Test
    void forgotPasswordTest() {
        String email = "laneyij534@akoption.com";
        authService.forgotPassword(email);
    }

    @Test
    void shouldThrowUserNotFoundExceptionIfUserNotExists() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> authService.forgotPassword("sdbc@gmail.com"), () -> "given email is not registered with us");
    }

    @Test
    void shouldSendEmailIfUserExists() {
//        String email = "laneyij534@akoption.com";
//
//        authService.forgotPassword(email);

//        Mockito.verify(resetPasswordTokenService, Mockito.times(1))
//                .generatePasswordTokenForUser();
//        Mockito.verify(resetPasswordTokenService, Mockito.times(1))
//                .calculateExpirationDate(Mockito.anyLong());
//        Mockito.verify(mailService, Mockito.times(1)).send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

    }

    @Test
    public void test_valid_email_and_password() {
        // Mock dependencies


        Authentication authentication = new UsernamePasswordAuthenticationToken("valid_name", "valid_password");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(customAuthenticationProvider.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(anyString())).thenReturn("valid_token");

        UserWelcomeResponse response = null;
        try {
            response = authService.signIn(userSignInRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Verify the result
        assertEquals("valid_token", response.getToken());
        assertEquals("valid_name", response.getFirstName());

        verify(customAuthenticationProvider, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(anyString());
    }

    @Test
    public void test_invalid_email_and_password() {

        Authentication authentication = new UsernamePasswordAuthenticationToken(userSignInRequest.getEmailOrUsername(), userSignInRequest.getPassword());

        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Stub the behavior of authenticate method to throw BadCredentialsException
        when(customAuthenticationProvider.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        when(jwtService.generateToken(anyString())).thenReturn("valid_token");

        // Verify that the signIn method throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> authService.signIn(userSignInRequest));
    }

    @Test
    public void testOAuth2SignIn_Success() {
        // Mock the OAuth2 user
        UserSignInRequest userSignInRequest = new UserSignInRequest();
        userSignInRequest.setAuthenticationType(AuthenticationType.OAUTH2);

        Authentication authentication = new UsernamePasswordAuthenticationToken("valid_name", userSignInRequest.getPassword());

        when(customAuthenticationProvider.authenticateForOAuth2(any())).thenReturn(authentication);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(jwtService.generateToken(userSignInRequest.getEmailOrUsername())).thenReturn("valid_token");

        // Call the method
        UserWelcomeResponse response = null;
        try {
            response = authService.signIn(userSignInRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Verify the result
        assertEquals("valid_token", response.getToken());
        assertEquals("valid_name", response.getFirstName());

        verify(customAuthenticationProvider, times(1)).authenticateForOAuth2(any());
        verify(jwtService, times(1)).generateToken(userSignInRequest.getEmailOrUsername());
    }





}



