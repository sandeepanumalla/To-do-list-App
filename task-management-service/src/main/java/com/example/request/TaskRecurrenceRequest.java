package com.example.request;

import com.example.model.DayOfWeek;
import com.example.model.RecurrenceType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class TaskRecurrenceRequest {
    private RecurrenceType type;
    private int recurrenceInterval;
    private Set<DayOfWeek> weeklyDays;
    private int dayOfMonth;
    private int month;
}
