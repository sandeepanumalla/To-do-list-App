package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
public class Step {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private int sequence;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @NotNull
    private TaskStepStatus completionStatus = TaskStepStatus.PENDING;
}
