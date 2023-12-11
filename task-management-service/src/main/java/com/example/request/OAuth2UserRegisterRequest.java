package com.example.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link UserRegisterRequest}
 */
@Value
@Data
@Builder
public class OAuth2UserRegisterRequest implements Serializable {
    @NotNull
    @NotEmpty(message = "please provide a username")
    String username;

    @NotNull
    @Pattern(message = "please provide valid email", regexp = "^[A-Za-z0-9+.-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$")
    @NotEmpty(message = "please provide an email")
    String email;

    @NotNull
    @NotEmpty(message = "please provide firstName")
    String firstName;

    @NotNull
    @NotEmpty(message = "please provide lastName")
    String lastName;

    @NotNull
    @Size(message = "password must be between 8 and 20 characters", min = 8, max = 20)
    @Pattern(message = "password must contain both alphabets and numerics", regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]+$")
    @NotEmpty(message = "please provide a password")
    String password;

    @NotNull
    @NotEmpty(message = "please confirm the password")
    String confirmPassword;
}