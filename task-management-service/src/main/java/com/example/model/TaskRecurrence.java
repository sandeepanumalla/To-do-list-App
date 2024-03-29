package com.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Month;

@Getter
@Setter
@Entity
@Table(name = "task_recurrence")
public class TaskRecurrence {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private RecurrenceType type;
    private int recurrenceInterval; // For specifying every nth day, week, month, etc.

    private int weeklyDays; // For weekly recurrence
    private int dayOfMonth; // For monthly recurrence
    private int month; // For yearly recurrence
    // Additional fields for custom recurrence pattern

    @OneToOne
    private Task task;

}

