package com.example.service;

import com.example.model.Step;
import com.example.request.TaskStepRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskStepService {

    // Create a step for a task
    Step createStep(Long taskId, TaskStepRequest step);

    // Get all steps for a task
    List<Step> getAllSteps(Long taskId);

    // Get a specific step for a task
    Step getStep(Long taskId, Long stepId);

    // Update a step for a task
    Step updateStep(Long taskId, Long stepId, Step step);

    // Delete a step for a task
    void deleteStep(Long taskId, Long stepId);
}
