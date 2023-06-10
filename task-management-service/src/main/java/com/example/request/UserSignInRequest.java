package com.example.request;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignInRequest {

    @ValidEmailOrUsername(message = "please provide email or username")
    String emailOrUsername;

    @NotEmpty(message = "please provide password")
    String password;
}
class ValidEmailOrUsernameValidator implements ConstraintValidator<ValidEmailOrUsername, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isValidEmail(value) || isValidUsername(value);
    }

    private boolean isValidEmail(String value) {
        if (value == null) {
            return false;
        }
        // Check if value is a valid email
        // Here's a basic example using a regular expression pattern

        // Regular expression for email validation
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return value.matches(emailPattern);
    }

    private boolean isValidUsername(String value) {
        if (value == null) {
            return false;
        }
        // Check if value is a valid username
        // Implement your username validation logic here

        // Return true if value is a valid username, false otherwise
        return !value.isEmpty(); // Example: Assume any non-empty string is a valid username
    }
}

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmailOrUsernameValidator.class)
@interface ValidEmailOrUsername {
    String message() default "Invalid email or username";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
