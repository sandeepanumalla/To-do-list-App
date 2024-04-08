package com.example.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.example.model.Step}
 */
@Value
@Setter
@Getter
public class TaskStepRequest implements Serializable {

    @NotNull
    @NotEmpty
    @NotBlank
    String name;


    Integer sequence;

}
