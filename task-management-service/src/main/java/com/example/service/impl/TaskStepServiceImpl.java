package com.example.service.impl;

import com.example.model.Step;
import com.example.model.Task;
import com.example.model.User;
import com.example.repository.TaskRepository;
import com.example.repository.TaskStepRepository;
import com.example.request.TaskStepRequest;
import com.example.request.TaskStepUpdateRequest;
import com.example.response.TaskResponse;
import com.example.response.TaskStepResponse;
import com.example.service.TaskStepService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.comparator.Comparators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class TaskStepServiceImpl implements TaskStepService {

    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;
    private final TaskStepRepository taskStepRepository;

    private final TaskServiceImpl taskService;


    public TaskStepServiceImpl(TaskRepository taskRepository, ModelMapper modelMapper,
                               TaskStepRepository taskStepRepository, TaskServiceImpl taskService) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.taskStepRepository = taskStepRepository;
        this.taskService = taskService;
    }

    @Override
    public TaskStepResponse createStep(Long taskId, TaskStepRequest stepRequest) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        // Validate step data
        if (stepRequest == null) {
            throw new IllegalArgumentException("Step object cannot be null");
        }
        // Perform additional validations as needed

        // Set task for the step
        Step step = modelMapper.map(stepRequest, Step.class);

        if(stepRequest.getSequence() != null) {
            step.setSequence(stepRequest.getSequence());
        } else {
            Integer maxSequence = task.getSteps().stream()
                    .mapToInt(Step::getSequence)
                    .boxed()
                    .max(Comparator.naturalOrder())
                    .orElse(0);

            step.setSequence(maxSequence + 1);
        }
        step.setTask(task);
        Step savedStep = taskStepRepository.save(step);
        if (task.getSteps() == null) {
            task.setSteps(new ArrayList<>());
        }

        // Add the saved step to the steps list
        task.getSteps().add(savedStep);
        taskRepository.save(task);

        // Save the step
        return modelMapper.map(savedStep, TaskStepResponse.class);
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
    public TaskStepResponse updateStep(Long taskId, Long stepId, TaskStepUpdateRequest taskStepRequest, User user) {

        Task task = getTaskById(taskId);

        if(!taskService.checkTaskOwnership(task, user.getUserId())) {
            throw new IllegalStateException("Task not owned by user");
        }

        // Retrieve the existing step
        Step existingStep = getStepByIdInTask(task, stepId);
        if(taskStepRequest.getName() != null) {
            existingStep.setName(taskStepRequest.getName());
        }

            // Retrieve the new sequence from the request
            Integer newSequence = taskStepRequest.getSequence();

            // Get the current sequence of the existing step
            Integer currentSequence = existingStep.getSequence();


                if(taskStepRequest.getSequence() != null) {

                        // Adjust the sequences of other steps in the task to maintain order
                        Step stepToBeReplaced = task.getSteps().stream().skip(newSequence - 1).limit(1).findFirst().orElse(null);
                        stepToBeReplaced.setSequence(currentSequence);

                    existingStep.setSequence(taskStepRequest.getSequence());
                }
                if(taskStepRequest.getStatus() != null) {
                    existingStep.setCompletionStatus(taskStepRequest.getStatus());
                }

            taskStepRepository.save(existingStep);
        return modelMapper.map(existingStep, TaskStepResponse.class);
    }

    @Override
    public void deleteStep(Long taskId, Long stepId, User user) {
        Task task = getTaskById(taskId);

        if(!taskService.checkTaskOwnership(task, user.getUserId())) {
            throw new IllegalStateException("Task not owned by user");
        }

        // Retrieve the existing step
        Step existingStep = getStepByIdInTask(task, stepId);
        // Remove the step from the task's list of steps
        task.getSteps().remove(existingStep);

        // Save the task (which will cascade the delete operation to the step)
        taskRepository.save(task);
//        taskStepRepository.delete(existingStep);

    }

    private Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
    }

    private Step getStepByIdInTask(Task task, Long stepId) {
        return task.getSteps().stream()
                .filter(s -> s.getId().equals(stepId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Step not found with id: " + stepId + " for task: " + task.getId()));
    }
    @Override
    public TaskResponse promoteStepToTask(Long currentTaskId, Long newTaskId, User user, TaskStepRequest taskStepRequest) {
        Task newTask = new Task();
        newTask.setTitle("New Task Name"); // Set task name

        Task existingTask = getTaskById(currentTaskId);


        if(!taskService.checkTaskOwnership(existingTask, user.getUserId())) {
            throw new IllegalStateException("Task not owned by user");
        }

        Step step = modelMapper.map(taskStepRequest, Step.class);

        step.setTask(newTask);

        newTask.getSteps().add(step);

        Task savedTask = taskRepository.save(newTask);

        return modelMapper.map(savedTask, TaskResponse.class);
    }

}
