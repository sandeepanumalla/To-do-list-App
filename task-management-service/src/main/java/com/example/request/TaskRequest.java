package com.example.request;


import com.example.model.Category;
import com.example.model.Priority;
import com.example.model.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequest {
    @NotBlank(message = "title should not be blank")
    @NotEmpty(message = "title is required")
    private String title;

    private String description;

    private LocalDateTime dueDate;

    private Priority priority;

    private Category category;

    private long userId;

}
