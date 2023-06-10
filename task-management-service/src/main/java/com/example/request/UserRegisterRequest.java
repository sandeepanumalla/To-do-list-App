package com.example.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterRequest {

    @NotEmpty
    String username;

    @NotEmpty
    String email;

    @NotEmpty
    String password;

    @NotEmpty
    String confirmPassword;

}
