package com.example.Controller;

import com.example.config.RestEndpoints;
import com.example.model.*;
import com.example.request.*;
import com.example.response.ReminderResponse;
import com.example.response.TaskResponse;
import com.example.service.ReminderService;
import com.example.service.TaskSharingService;
import com.example.service.TaskService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RestEndpoints.TASKS)
public class TaskController extends GenericUpdateController<Task, Long>{

    private final TaskService taskService;
    private final UserService userService;
    private final ReminderService reminderService;
    private final TaskSharingService sharedTaskService;

    private final ModelMapper modelMapper;


    @Autowired
    public TaskController(TaskService taskService, UserService userService, ReminderService reminderService, TaskSharingService sharedTaskService, ModelMapper modelMapper) {
        this.taskService = taskService;
        this.userService = userService;
        this.reminderService = reminderService;
        this.sharedTaskService = sharedTaskService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(RestEndpoints.CREATE_TASK)
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskRequest taskRequest, HttpServletRequest httpServletRequest) throws Exception {
        User owner = userService.getUserIdByToken(httpServletRequest);
        TaskResponse taskResponse = taskService.createTask(owner, taskRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @DeleteMapping(RestEndpoints.DELETE_TASK)
    public ResponseEntity<?> deleteTaskById(@PathVariable long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok().body("task has been deleted successfully");
    }

    @PutMapping(RestEndpoints.UPDATE_TASK)
    public ResponseEntity<?> updateTaskById(@RequestBody TaskUpdateRequest taskUpdateRequest, @PathVariable String taskId) {
        taskUpdateRequest.setId(Long.parseLong(taskId));
        TaskResponse taskResponse = taskService.updateTask(taskUpdateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @GetMapping(RestEndpoints.GET_TASK_BY_TASK_ID)
    public ResponseEntity<?> getTaskById(@PathVariable(name = "taskId") long taskId) {
        TaskResponse taskResponse= taskService.getTaskById(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponse);
    }

    @PutMapping(RestEndpoints.ADD_CATEGORY_FROM_TASK)
    public ResponseEntity<?> addCategoryToTask(@PathVariable("taskId") Long taskId, @PathVariable("categoryId") Long categoryId, HttpServletRequest httpServletRequest) {
        User owner = userService.getUserIdByToken(httpServletRequest);
        try {
            taskService.addCategoryToTask(owner.getUserId(), taskId, categoryId);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.OK).body("task added to the category");
    }

    @DeleteMapping(RestEndpoints.REMOVE_CATEGORY_FROM_TASK)
    public ResponseEntity<?> removeCategoryFromTask(@PathVariable("taskId") Long taskId, @PathVariable("categoryId") Long categoryId, HttpServletRequest httpServletRequest) {
        User owner = userService.getUserIdByToken(httpServletRequest);
        try {
            taskService.removeCategoryFromTask(owner.getUserId(), taskId, categoryId);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.OK).body("task removed from the category");
    }

    @PostMapping(RestEndpoints.SET_REMINDER)
    public ResponseEntity<?> setReminder(@PathVariable("taskId") Long taskId, @RequestBody ReminderRequest reminderRequest, HttpServletRequest httpServletRequest) {
        User owner = userService.getUserIdByToken(httpServletRequest);
        ReminderResponse savedReminder = reminderService.setReminder(taskId, owner.getUserId(), reminderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReminder);
    }

    @GetMapping(RestEndpoints.GET_REMINDERS)
    public ResponseEntity<?> getReminders(@PathVariable("taskId") Long taskId, HttpServletRequest request) {
        User user  = userService.getUserIdByToken(request);
        List<ReminderResponse> reminders = reminderService.getReminders(taskId, user.getUserId());
        reminders.stream().filter(reminder -> reminder.getId() != 2).mapToLong(( ReminderResponse::getId));
        return ResponseEntity.ok().body(reminders);

    }

    @DeleteMapping(RestEndpoints.DELETE_REMINDER)
    public ResponseEntity<?> deleteReminders(@PathVariable("taskId") Long taskId, @PathVariable("reminderId") Long reminderId, HttpServletRequest request) {
        User user  = userService.getUserIdByToken(request);
        reminderService.deleteReminder(taskId, reminderId, user.getUserId());
        return ResponseEntity.ok().body("reminder deleted successfully");
    }

    @PutMapping(RestEndpoints.SHARE_THE_TASK)
    public ResponseEntity<?> shareTheTask(@CookieValue("jwt") String CookieValue, @PathVariable("taskId") Long taskId,@Valid @RequestBody TaskShareRequest taskShareRequest,
                                          HttpServletRequest request) {
        System.out.println("cookie " + CookieValue);
        System.out.println(userService.getUserIdByToken(CookieValue).getUsername());
        User owner = userService.getUserIdByToken(request);
        taskShareRequest.setTaskId(taskId);
        taskShareRequest.setTaskOwner(owner);
        sharedTaskService.share(taskShareRequest);
        return ResponseEntity.ok("task has been shared");
    }

    @DeleteMapping(RestEndpoints.UNSHARE_THE_TASK)
    public ResponseEntity<?> unshareTheTask(@PathVariable("taskId") Long taskId,@Valid @RequestBody TaskUnShareRequest taskUnShareRequest,
                                            HttpServletRequest request) {
        User owner = userService.getUserIdByToken(request);
        taskUnShareRequest.setTaskId(taskId);
        taskUnShareRequest.setTaskOwner(owner);

        sharedTaskService.unShare(taskUnShareRequest);

        return ResponseEntity.ok("Task has been unshared with selected users.");
    }

    @PatchMapping("/{id}/isImportant")
    public ResponseEntity<?> updateIsImportantField(@PathVariable Long id, @RequestBody boolean newValue) {
        ResponseEntity<?>  response= updateField(id, "isImportant", newValue);
        if (response.getStatusCode().is2xxSuccessful()) {
            Task updatedTask = (Task) response.getBody();
            TaskResponse taskResponseDTO = modelMapper.map(updatedTask, TaskResponse.class);
            return ResponseEntity.ok(taskResponseDTO);
        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }

    @PatchMapping("/{id}/title")
    public ResponseEntity<?> updateTitleField(@PathVariable Long id, @RequestBody String newValue) {
        ResponseEntity<?>  response = updateField(id, "title", newValue);
        if (response.getStatusCode().is2xxSuccessful()) {
            Task updatedTask = (Task) response.getBody();
            TaskResponse taskResponseDTO = modelMapper.map(updatedTask, TaskResponse.class);
            return ResponseEntity.ok(taskResponseDTO);
        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<?> updateDescriptionField(@PathVariable Long id, @RequestBody String newValue) {
        ResponseEntity<?>  response = updateField(id, "description", newValue);
        if (response.getStatusCode().is2xxSuccessful()) {
            Task updatedTask = (Task) response.getBody();
            TaskResponse taskResponseDTO = modelMapper.map(updatedTask, TaskResponse.class);
            return ResponseEntity.ok(taskResponseDTO);
        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatusField(@PathVariable Long id, @RequestBody TaskStatus newValue) {
        ResponseEntity<?>  response = updateField(id, "taskStatus", newValue);
        if (response.getStatusCode().is2xxSuccessful()) {
            Task updatedTask = (Task) response.getBody();
            TaskResponse taskResponseDTO = modelMapper.map(updatedTask, TaskResponse.class);
            return ResponseEntity.ok(taskResponseDTO);
        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }

    @PatchMapping("/{id}/reminders")
    public ResponseEntity<?> updateRemindersField(@PathVariable Long id, @RequestBody List<Reminder> newValue) {
        ResponseEntity<?>  response = updateField(id, "reminders", newValue);
        if (response.getStatusCode().is2xxSuccessful()) {
            Task updatedTask = (Task) response.getBody();
            TaskResponse taskResponseDTO = modelMapper.map(updatedTask, TaskResponse.class);
            return ResponseEntity.ok(taskResponseDTO);
        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }

    @GetMapping // needs to change
    public ResponseEntity<?> getAllTasksForUserA(
            HttpServletRequest request,
            @RequestParam(name = "sort", defaultValue = "title") String sortBy,
            @RequestParam(name = "pageNum", defaultValue = "0", required = false) Integer pageNum,
            @RequestParam(name = "important", defaultValue = "defaultValue", required = false) String importantParam,                                               @RequestParam(name = "size", defaultValue = "5") Integer pageSize,
            @RequestParam(name = "status", required = false) String taskStatus,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "shared", required = false) String sharedWithParam
    ) {
        Boolean important = "defaultValue".equals(importantParam) ? null : Boolean.valueOf(importantParam);
        Boolean sharedWith = "defaultValue".equals(sharedWithParam) ? null : Boolean.valueOf(sharedWithParam);
        long userId = userService.getUserIdByToken(request).getUserId();
        boolean ascending = !sortBy.startsWith("-");
        SortOption validatedSortBy = validateSortBy(sortBy);

        TaskStatus status = taskStatus != null ? TaskStatus.valueOf(taskStatus.toUpperCase()) : null;
        Pageable pageable = PageRequest.of(0, pageSize, ascending ?
                Sort.by(Sort.Direction.ASC, validatedSortBy.getValue()) :
                Sort.by(Sort.Direction.DESC, validatedSortBy.getValue()));
        List<TaskResponse> taskResponses = userService.getAllTasksDup2(userId, status, important, category, sharedWith, pageable);
        return ResponseEntity.ok(taskResponses);
    }

    private SortOption validateSortBy(String sortBy) {
        try {
            sortBy = sortBy.replaceAll("^[+\\-]+", "");
            return SortOption.valueOf(sortBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SortOption.TITLE; // Default sorting option
        }
    }
}
