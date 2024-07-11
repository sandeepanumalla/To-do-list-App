package com.example.Controller;

import com.example.config.RestEndpoints;
import com.example.exceptions.TaskNotFoundException;
import com.example.model.*;
import com.example.repository.TaskRepository;
import com.example.request.*;
import com.example.response.CategoryResponse;
import com.example.response.ReminderResponse;
import com.example.response.TaskRecurrenceResponse;
import com.example.response.TaskResponse;
import com.example.service.*;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@RestController
@RequestMapping(RestEndpoints.TASKS)
public class TaskController extends GenericUpdateController<Task, Long>
{

    private final TaskService taskService;
    private final UserService userService;
    private final ReminderService reminderService;
    private final TaskSharingService sharedTaskService;

    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final TaskRecurrenceService taskRecurrenceService;


    @Autowired
    public TaskController(TaskService taskService, UserService userService, ReminderService reminderService, TaskSharingService sharedTaskService, ModelMapper modelMapper,
                          TaskRepository taskRepository, TaskRecurrenceService taskRecurrenceService) {
        this.taskService = taskService;
        this.userService = userService;
        this.reminderService = reminderService;
        this.sharedTaskService = sharedTaskService;
        this.modelMapper = modelMapper;
        this.taskRepository = taskRepository;
        this.taskRecurrenceService = taskRecurrenceService;
    }

    @PostMapping(RestEndpoints.CREATE_TASK)
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskRequest taskRequest, HttpServletRequest httpServletRequest) throws Exception {
        User owner = userService.getUserIdByToken(httpServletRequest);
        TaskResponse taskResponse = taskService.createTask(owner, taskRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @DeleteMapping(RestEndpoints.DELETE_TASK)
    public ResponseEntity<?> deleteTaskById(@PathVariable long taskId, HttpServletRequest httpServletRequest) {
        User owner = userService.getUserIdByToken(httpServletRequest);

        taskService.deleteTask(owner, taskId);
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
        return ResponseEntity.status(HttpStatus.OK).body("task added to the categorych");
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

    @PostMapping(RestEndpoints.SHARE_THE_TASK)
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

    @PatchMapping(RestEndpoints.UNSHARE_THE_TASK)
    public ResponseEntity<?> unShareTheTask(@PathVariable("taskId") Long taskId,@Valid @RequestBody TaskUnShareRequest taskUnShareRequest,
                                            HttpServletRequest request) {
        User owner = userService.getUserIdByToken(request);
        taskUnShareRequest.setTaskId(taskId);
        taskUnShareRequest.setTaskOwner(owner);

        sharedTaskService.unShare(taskUnShareRequest);

        return ResponseEntity.ok("Task has been unshared with selected users.");
    }

    @PatchMapping("/{id}/isImportant")
    public ResponseEntity<?> updateIsImportantField(@PathVariable Long id,
                                                    HttpServletRequest httpServletRequest,
                                                    @RequestBody boolean newValue) {
        User owner = userService.getUserIdByToken(httpServletRequest);
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        boolean isOwner = taskService.checkTaskOwnership(task, owner.getUserId());
        if(!isOwner) {
            throw  new RuntimeException("You are not the owner of this task");
        }

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
    public ResponseEntity<?> updateTitleField(@PathVariable Long id, @RequestBody String newValue, HttpServletRequest httpServletRequest) {
        User owner = userService.getUserIdByToken(httpServletRequest);
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        boolean isOwner = taskService.checkTaskOwnership(task, owner.getUserId());
        if(!isOwner) {
            throw  new RuntimeException("You are not the owner of this task");
        }
        if (newValue.length() > 100) {
            throw new RuntimeException("Title cannot be longer than 100 characters");
        }
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
    public ResponseEntity<?> updateDescriptionField(@PathVariable Long id, @RequestBody String newValue, HttpServletRequest httpServletRequest) {
        User owner = userService.getUserIdByToken(httpServletRequest);
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        boolean isOwner = taskService.checkTaskOwnership(task, owner.getUserId());
        if(!isOwner) {
            throw  new RuntimeException("You are not the owner of this task");
        }
        if (newValue.length() > 1000) {
            throw new RuntimeException("Description cannot be longer than 1000 characters");
        }
        ResponseEntity<?>  response = updateField(id, "description", newValue);
        if (response.getStatusCode().is2xxSuccessful()) {
            Task updatedTask = (Task) response.getBody();
            TaskResponse taskResponseDTO = modelMapper.map(updatedTask, TaskResponse.class);
            return ResponseEntity.ok(taskResponseDTO);
        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }

    @PatchMapping("/{id}/due-date")
    public ResponseEntity<?> updateDueDateField(@PathVariable Long id, @RequestBody LocalDate newDueDate, HttpServletRequest httpServletRequest) {
        User owner = userService.getUserIdByToken(httpServletRequest);
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        boolean isOwner = taskService.checkTaskOwnership(task, owner.getUserId());
        if(!isOwner) {
            throw  new RuntimeException("You are not the owner of this task");
        }
        ResponseEntity<?>  response = updateField(id, "dueDate", newDueDate);
        if (response.getStatusCode().is2xxSuccessful()) {
            User user = userService.getUserIdByToken(httpServletRequest);
            Task updatedTask = (Task) response.getBody();
            if(newDueDate.isEqual(LocalDate.now())) {
                taskService.addTaskToMyDay(updatedTask.getId(), user);
            }
            TaskResponse taskResponseDTO = modelMapper.map(updatedTask, TaskResponse.class);
            return ResponseEntity.ok(taskResponseDTO);
        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatusField(@PathVariable Long id, HttpServletRequest httpServletRequest, @RequestBody TaskStatus newValue) {
        User owner = userService.getUserIdByToken(httpServletRequest);
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        boolean isOwner = taskService.checkTaskOwnership(task, owner.getUserId());
        if(!isOwner) {
            throw  new RuntimeException("You are not the owner of this task");
        }

        ResponseEntity<?>  response = updateField(id, "taskStatus", newValue);
        if (response.getStatusCode().is2xxSuccessful()) {
            Task updatedTask = (Task) response.getBody();
            TaskResponse taskResponseDTO = modelMapper.map(updatedTask, TaskResponse.class);
            return ResponseEntity.ok(taskResponseDTO);
        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }

    @PatchMapping("/{id}/reminder")
    public ResponseEntity<?> updateRemindersField(@PathVariable Long id, @RequestBody Reminder newValue) {
//        ResponseEntity<?>  response = updateField(id, "reminders", newValue);
        // call the cache update.
        ReminderResponse updateReminder = reminderService.updateReminder(id,  newValue);

        return ResponseEntity.ok(updateReminder);
    }



//    @GetMapping // needs to change
//    public ResponseEntity<?> getAllTasksForUserA(
//            HttpServletRequest request,
//            @RequestParam(name = "sort", defaultValue = "title") String sortBy,
//            @RequestParam(name = "pageNum", defaultValue = "0", required = false) Integer pageNum,
//            @RequestParam(name = "important", defaultValue = "defaultValue", required = false) String importantParam,                                               @RequestParam(name = "size", defaultValue = "5") Integer pageSize,
//            @RequestParam(name = "status", required = false) String taskStatus,
//            @RequestParam(name = "category", required = false) String category,
//            @RequestParam(name = "shared", required = false) String sharedWithParam,
//            @RequestParam(name = "myDay", required = false) String myDayParam
//    ) {
//        Boolean important = "defaultValue".equals(importantParam) ? null : Boolean.valueOf(importantParam);
//        Boolean sharedWith = "defaultValue".equals(sharedWithParam) ? null : Boolean.valueOf(sharedWithParam);
//        User user = userService.getUserIdByToken(request);
//        boolean ascending = !sortBy.startsWith("-");
//        SortOption validatedSortBy = validateSortBy(sortBy);
//
//        TaskStatus status = taskStatus != null ? TaskStatus.valueOf(taskStatus.toUpperCase()) : null;
//        Pageable pageable = PageRequest.of(0, pageSize, ascending ?
//                Sort.by(Sort.Direction.ASC, validatedSortBy.getValue()) :
//                Sort.by(Sort.Direction.DESC, validatedSortBy.getValue()));
//
//        List<TaskResponse> taskResponses = taskService.getAllTasksDup2(user.getUserId(), status, important, category, sharedWith, pageable);
//        return ResponseEntity.ok(taskResponses);
//    }


    @GetMapping
    public ResponseEntity<?> getAllTasks(
            HttpServletRequest request,
            @RequestParam(name = "sort", defaultValue = "title") String sortBy,
            @RequestParam(name = "pageNum", defaultValue = "0", required = false) Integer pageNum,
            @RequestParam(name = "important", defaultValue = "defaultValue", required = false) String importantParam,
            @RequestParam(name = "size", defaultValue = "5") Integer pageSize,
            @RequestParam(name = "status", required = false) String taskStatus,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "shared", required = false) String sharedWithParam,
            @RequestParam(name = "myDay", required = false) String myDayParam,
            @RequestParam(name = "filter", required = false) String filterParam
    ) {
        Boolean important = "defaultValue".equals(importantParam) ? null : Boolean.valueOf(importantParam);
        Boolean sharedWith = "defaultValue".equals(sharedWithParam) ? null : Boolean.valueOf(sharedWithParam);
        boolean ascending = !sortBy.startsWith("-");
        SortOption validatedSortBy = validateSortBy(sortBy);


        TaskStatus status = taskStatus != null ? TaskStatus.valueOf(taskStatus.toUpperCase()) : null;
        Pageable pageable = PageRequest.of(pageNum, pageSize, ascending ?
                Sort.by(Sort.Direction.ASC, validatedSortBy.getValue()) :
                Sort.by(Sort.Direction.DESC, validatedSortBy.getValue()));

        User user = userService.getUserIdByToken(request);
        List<TaskResponse> taskResponses;

        if ("true".equals(myDayParam)) {
            taskResponses = taskService.fetchMyDayTasks(user);
        } else {
            taskResponses = taskService.getAllTasksDup2(user.getUserId(), status, important, category, sharedWith, pageable);
        }

        Map<String, List<TaskResponse>> groupedTasks = new HashMap<>();

        if(filterParam != null) {
            switch (filterParam.toLowerCase()) {
                case "myday":
                    return ResponseEntity.ok(groupByMyDay(groupedTasks, taskResponses));
                case "planned":
                    return ResponseEntity.ok(groupByPlanned(groupedTasks, taskResponses));
                case "all":
                    return ResponseEntity.ok(groupByCategory(groupedTasks, taskResponses));
                case "completed":
                    return ResponseEntity.ok(groupByCategoryAndCompleted(groupedTasks, taskResponses));
                default:
                    throw new IllegalArgumentException("Invalid filter parameter: " + filterParam);
            }
        }


        return ResponseEntity.ok(taskResponses);
    }


    private Map<String, List<TaskResponse>> groupByMyDay(Map<String, List<TaskResponse>> groupedTasks, List<TaskResponse> tasks) {
        groupedTasks.put("completed", tasks.stream().filter(taskResponse -> taskResponse.getTaskStatus() == TaskStatus.COMPLETED).collect(Collectors.toList()));
        groupedTasks.put("incomplete", tasks.stream().filter(taskResponse -> taskResponse.getTaskStatus() != TaskStatus.COMPLETED).collect(Collectors.toList()));
        return groupedTasks;
    }

    private Map<String, List<TaskResponse>> groupByPlanned(Map<String, List<TaskResponse>> groupedTasks, List<TaskResponse> tasks) {
        groupedTasks.put("earlier", tasks.stream()
                .filter(taskResponse -> !taskResponse.getTaskStatus().equals(TaskStatus.COMPLETED)
                        && taskResponse.getDueDate().isBefore(LocalDate.now())).collect(Collectors.toList()));
        groupedTasks.put("today", tasks.stream()
                .filter(taskResponse -> !taskResponse.getTaskStatus().equals(TaskStatus.COMPLETED)
                && taskResponse.getDueDate().isEqual(LocalDate.now())).collect(Collectors.toList()));
        groupedTasks.put("upcoming", tasks.stream()
                .filter(taskResponse -> !taskResponse.getTaskStatus().equals(TaskStatus.COMPLETED)
                && taskResponse.getDueDate().isAfter(LocalDate.now())).collect(Collectors.toList()));
        return groupedTasks;
    }



    private Map<String, List<TaskResponse>> groupByCategoryAndCompleted(Map<String, List<TaskResponse>> groupTasks, List<TaskResponse> tasks) {
        return tasks.stream()
                .filter(taskResponse -> taskResponse.getTaskStatus().equals(TaskStatus.COMPLETED))
                .collect(groupingBy(taskResponse -> taskResponse.getCategory().toString()));
    }


    private Map<String, List<TaskResponse>> groupByCategory(Map<String, List<TaskResponse>> groupedTasks, List<TaskResponse> tasks) {
        return tasks.stream().collect(groupingBy(taskResponse -> taskResponse.getCategory().toString(), Collectors.toList()));
    }


    @GetMapping("/summary")
    public ResponseEntity<?> getTaskSummary(HttpServletRequest request) {
        User user = userService.getUserIdByToken(request);
        Map<String, List<Long>> taskSummary = taskService.taskSummary(user);
        return ResponseEntity.ok(taskSummary);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("keywords") String keywords, HttpServletRequest request) {
        User user = userService.getUserIdByToken(request);
        List<TaskResponse> taskResponses = taskService.searchTaskByTitle(user, keywords);
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

    @RequestMapping(path = "/{taskId}/my-day", method = RequestMethod.POST)
    public ResponseEntity<?> addTaskToMyDay(HttpServletRequest request, @PathVariable Long taskId) {
        User user = userService.getUserIdByToken(request);
        taskService.addTaskToMyDay(taskId, user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Task added to My Day successfully.");
    }

    @RequestMapping(path = "/{taskId}/my-day", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeTaskFromMyDay(HttpServletRequest request, @PathVariable Long taskId) {
        User user = userService.getUserIdByToken(request);
        taskService.removeTaskFromMyDay(taskId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/recurrence")
    public ResponseEntity<TaskRecurrenceResponse> createTaskRecurrence(@PathVariable Long taskId,
                                                                       @RequestBody TaskRecurrenceRequest taskRecurrenceRequest ,HttpServletRequest request) {
        User user = userService.getUserIdByToken(request);
        Task task = taskRepository.findById(taskId).orElseThrow();
        taskService.checkTaskOwnership(task, user.getUserId());
        TaskRecurrenceResponse taskRecurrenceResponse = taskRecurrenceService.createTaskRecurrence(taskId, taskRecurrenceRequest);
        return ResponseEntity.ok().body(taskRecurrenceResponse);
    }

    @DeleteMapping("/{taskId}/recurrence")
    public ResponseEntity<?> deleteTaskRecurrence(@PathVariable Long taskId, HttpServletRequest request) {
        User user = userService.getUserIdByToken(request);
        taskRecurrenceService.deleteTaskRecurrence(taskId);
        return ResponseEntity.noContent().build();
    }


}
