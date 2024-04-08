package com.example.service;

import com.example.model.Step;
import com.example.model.User;
import com.example.request.TaskStepRequest;
import com.example.request.TaskStepUpdateRequest;
import com.example.response.TaskResponse;
import com.example.response.TaskStepResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskStepService {

    // Create a step for a task
    public TaskStepResponse createStep(Long taskId, TaskStepRequest stepRequest);

    // Get all steps for a task
    List<Step> getAllSteps(Long taskId);

    // Get a specific step for a task
    Step getStep(Long taskId, Long stepId);

    // Update a step for a task

    Step updateStep(Long taskId, Long stepId, TaskStepUpdateRequest taskStepRequest, User user);

    // Delete a step for a task
    void deleteStep(Long taskId, Long stepId, User user);

    public TaskResponse promoteStepToTask(Long currentTaskId, Long newTaskId, User user, TaskStepRequest taskStepRequest);
}
