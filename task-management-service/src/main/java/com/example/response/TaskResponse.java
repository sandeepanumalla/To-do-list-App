package com.example.response;

import com.example.model.*;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.List;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse implements Serializable {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private CategoryResponse category;
    private LocalDateTime creationDate;
    private LocalDateTime completionDate;
    private boolean isImportant = false;
    private TaskStatus taskStatus = TaskStatus.PENDING;
    private List<ReminderResponse> reminders;
}
