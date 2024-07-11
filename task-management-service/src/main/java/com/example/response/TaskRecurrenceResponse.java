package com.example.response;

import com.example.model.DayOfWeek;
import com.example.model.RecurrenceType;
import com.example.model.Task;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class TaskRecurrenceResponse {


        private Long id;

        private RecurrenceType type;
        private int recurrenceInterval; // For specifying every nth day, week, month, etc.

        private Set<DayOfWeek> weeklyDays; // For weekly recurrence
        private int dayOfMonth; // For monthly recurrence
        private int month; // For yearly recurrence
        // Additional fields for custom recurrence pattern

        private TaskResponse task;

}
