package com.example.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.example.model.Step}
 */
@Value
public record TaskStepRequest(@NotNull @NotEmpty @NotBlank String name, int sequence) implements Serializable {
}
