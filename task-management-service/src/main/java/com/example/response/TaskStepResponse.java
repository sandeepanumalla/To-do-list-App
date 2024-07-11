package com.example.response;

import com.example.model.TaskStepStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskStepResponse {

        public Long id;

        public String name;

        public Integer sequence;

        public TaskStepStatus completionStatus;
}
