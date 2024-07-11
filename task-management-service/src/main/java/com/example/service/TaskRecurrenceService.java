package com.example.service;

import com.example.model.TaskRecurrence;
import com.example.request.TaskRecurrenceRequest;
import com.example.response.TaskRecurrenceResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public interface TaskRecurrenceService {

    public TaskRecurrenceResponse createTaskRecurrence(Long taskId, TaskRecurrenceRequest taskRecurrence);

    public List<TaskRecurrenceResponse> getAllTaskRecurrences();

    public TaskRecurrenceResponse getTaskRecurrenceById(Long id);

    public TaskRecurrenceResponse updateTaskRecurrence(Long id, TaskRecurrenceRequest taskRecurrence);

    public boolean deleteTaskRecurrence(Long id);
}
