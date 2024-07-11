package com.example.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.*;
import java.util.stream.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@ToString
@Table(name = "task_recurrence")
public class TaskRecurrence {

    public TaskRecurrence() {
        this.weeklyDays = new HashSet<DayOfWeek>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private RecurrenceType type;
    private int recurrenceInterval; // For specifying every nth day, week, month, etc.

    @ElementCollection
    @Nullable
    @CollectionTable(name = "weekly_days", joinColumns = @JoinColumn(name = "task_recurrence_id"))
    @Column(name = "day_of_week", nullable = true)
    private Set<DayOfWeek> weeklyDays;
    private int dayOfMonth; // For monthly recurrence
    private int month; // For yearly recurrence

    private int year; // For yearly recurrence

    private LocalDate createdDate;

    @OneToOne
    private Task task;

    @AssertTrue(message = "Weekly days must be provided for weekly recurrence type")
    private boolean isValidWeeklyDays() {
        return type != RecurrenceType.WEEKLY || (weeklyDays != null && !weeklyDays.isEmpty());
    }

}

//if type == weekly ? (if weeklyDays == null || weeklyDays.isEmpty()) : false : true

