package com.example.service.impl;

import com.example.model.Task;
import com.example.model.TaskRecurrence;
import com.example.repository.TaskRecurrenceRepository;
import com.example.repository.TaskRepository;
import com.example.request.TaskRecurrenceRequest;
import com.example.response.TaskRecurrenceResponse;
import com.example.response.TaskResponse;
import com.example.service.TaskRecurrenceService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskRecurrenceServiceImpl implements TaskRecurrenceService {


    private final ModelMapper modelMapper;
    private final TaskRecurrenceRepository taskRecurrenceRepository;
    private final TaskRepository taskRepository;

    public TaskRecurrenceServiceImpl(ModelMapper modelMapper,
                                     TaskRecurrenceRepository taskRecurrenceRepository,
                                     TaskRepository taskRepository) {
        this.modelMapper = modelMapper;
        this.taskRecurrenceRepository = taskRecurrenceRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskRecurrenceResponse createTaskRecurrence(Long taskId, TaskRecurrenceRequest taskRecurrenceRequest) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        TaskRecurrence taskRecurrenceObject = modelMapper.map(taskRecurrenceRequest, TaskRecurrence.class);
        taskRecurrenceObject.setTask(task);
        taskRecurrenceObject.setWeeklyDays(taskRecurrenceRequest.getWeeklyDays());
        TaskRecurrence savedTaskRecurrenceObject = null;
        try {
            savedTaskRecurrenceObject = taskRecurrenceRepository.save(taskRecurrenceObject);
        } catch (DataIntegrityViolationException e) {
            // Handle the exception by returning a meaningful error response
            throw new RuntimeException("Data integrity violation occurred.");
        }
        return modelMapper.map(savedTaskRecurrenceObject, TaskRecurrenceResponse.class);
    }


    @Override
    public List<TaskRecurrenceResponse> getAllTaskRecurrences() {
        return List.of();
    }

    @Override
    public TaskRecurrenceResponse getTaskRecurrenceById(Long id) {
        TaskRecurrence taskRecurrence = taskRecurrenceRepository.findById(id).orElseThrow();
        return modelMapper.map(taskRecurrence, TaskRecurrenceResponse.class);
    }

    @Override
    public TaskRecurrenceResponse updateTaskRecurrence(Long id, TaskRecurrenceRequest taskRecurrence) {

        return null;
    }

    @Override
    public boolean deleteTaskRecurrence(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        taskRecurrenceRepository.deleteById(task.getTaskRecurrence().getId());
        task.setTaskRecurrence(null);
        taskRepository.save(task);
        return true;
    }


    @Scheduled(cron = "0 0 0 * * *")
    public void runner() throws SQLException {
        // TODO: fetch the tasks that are due in the next 24 hours
        List<TaskRecurrence> taskRecurrences = taskRecurrenceRepository.findAll();
        // if the task is a recurrence task, create a new task for each day that the task is due, if its completed then make it as incomplete
        for(TaskRecurrence taskRecurrence : taskRecurrences) {
            calculateNextFiringDate(taskRecurrence);
        }

        // if the task is not a recurrence task, then cleanup the recurrence
        // remind the user that the task is due in the next 24 hours
        // most likely this gonna create issues for MyDay. so maybe we should create a new task for the next 24 hours and push the current task to MyDay
    }

    public static LocalDate calculateNextFiringDate(TaskRecurrence taskRecurrence) {
        LocalDate currentDate = LocalDate.now();

        switch (taskRecurrence.getType()) {
            case DAILY:
                return currentDate.plusDays(taskRecurrence.getRecurrenceInterval());
            case WEEKLY:
                return calculateNextWeeklyFiringDate(taskRecurrence, currentDate);
            case MONTHLY:
                return calculateNextMonthlyFiringDate(taskRecurrence, currentDate);
            case YEARLY:
                return calculateNextYearlyFiringDate(taskRecurrence, currentDate);
            case CUSTOM:
                return calculateNextCustomFiringDate(taskRecurrence, currentDate);
            default:
                return null; // Handle unsupported recurrence types
        }
    }

    private static LocalDate calculateNextWeeklyFiringDate(TaskRecurrence taskRecurrence, LocalDate currentDate) {
        int daysToAdd = taskRecurrence.getRecurrenceInterval() * 7; // Add weeks
        return currentDate.plusDays(daysToAdd);
    }

    private static LocalDate calculateNextMonthlyFiringDate(TaskRecurrence taskRecurrence, LocalDate currentDate) {
        return currentDate.plusMonths(taskRecurrence.getRecurrenceInterval());
    }

    private static LocalDate calculateNextYearlyFiringDate(TaskRecurrence taskRecurrence, LocalDate currentDate) {
        return currentDate.plusYears(taskRecurrence.getRecurrenceInterval());
    }

    private static LocalDate calculateNextCustomFiringDate(TaskRecurrence taskRecurrence, LocalDate currentDate) {
        // Implement custom logic for calculating next firing date for custom recurrence
        // Example: Parse custom recurrence pattern and calculate next date accordingly
        switch (taskRecurrence.getType()) {
//            case WEEKDAYS:
//                return calculateNextWeekdaysFiringDate(taskRecurrence, currentDate);
//            case WEEKENDS:
//                return calculateNextWeekendsFiringDate(taskRecurrence, currentDate);
//            default:
//                return null; // Placeholder, replace with actual implementation
        }
//        switch ()
        return null; // Placeholder, replace with actual implementation
    }

    // implement the logic for calculating the next firing date for custom recurrence types
    // Example: WEEKDAYS, WEEKENDS, etc.



}
