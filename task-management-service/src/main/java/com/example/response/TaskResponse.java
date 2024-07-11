package com.example.response;

import com.example.model.*;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
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
    private LocalDate dueDate;
    private CategoryResponse category;
    private LocalDateTime creationDate;
    private LocalDateTime completionDate;
    private boolean isImportant = false;
    private boolean isPartOfMyDay = false;
    private TaskStatus taskStatus = TaskStatus.PENDING;
    private TaskRecurrenceResponse taskRecurrence;
    private List<TaskStepResponse> steps;
    private List<ReminderResponse> reminders;
}
