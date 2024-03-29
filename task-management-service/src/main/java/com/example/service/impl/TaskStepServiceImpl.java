package com.example.service.impl;

import com.example.model.Step;
import com.example.model.Task;
import com.example.repository.TaskRepository;
import com.example.repository.TaskStepRepository;
import com.example.request.TaskStepRequest;
import com.example.service.TaskService;
import com.example.service.TaskStepService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStepServiceImpl implements TaskStepService {


    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;
    private final TaskStepRepository taskStepRepository;

    private final TaskService taskService;

    public TaskStepServiceImpl(TaskRepository taskRepository, ModelMapper modelMapper,
                               TaskStepRepository taskStepRepository, TaskService taskService) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.taskStepRepository = taskStepRepository;
        this.taskService = taskService;
    }

    @Override
    public Step createStep(Long taskId, TaskStepRequest stepRequest) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        // Validate step data
        if (stepRequest == null) {
            throw new IllegalArgumentException("Step object cannot be null");
        }
        // Perform additional validations as needed

        // Set task for the step
        Step step = modelMapper.map(stepRequest, Step.class);

        // Save the step
        return taskStepRepository.save(step);
    }

    @Override
    public List<Step> getAllSteps(Long taskId) {
        return null;
    }

    @Override
    public Step getStep(Long taskId, Long stepId) {
        return null;
    }

    @Override
    public Step updateStep(Long taskId, Long stepId, Step step) {
        return null;
    }

    @Override
    public void deleteStep(Long taskId, Long stepId) {

    }
}
