package com.example.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.model.SortOption;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.config.RestEndpoints;
import com.example.model.Category;
import com.example.model.TaskStatus;
import com.example.model.User;
import com.example.repository.TaskRepository;
import com.example.response.TaskResponse;
import com.example.service.TaskSharingService;
import com.example.service.UserService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(RestEndpoints.USER)
//@CrossOrigin(allowCredentials = "true", origins = "{http://localhost:3000,http://localhost:8181}")
public class UserController  {
    private final UserService userService;
    private final TaskRepository taskRepository;

    private final TaskSharingService sharedTaskService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService,
                          TaskRepository taskRepository, TaskSharingService sharedTaskService,
                          ModelMapper modelMapper) {
        this.userService = userService;
        this.taskRepository = taskRepository;
        this.sharedTaskService = sharedTaskService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(RestEndpoints.GET_TASK_FOR_AUTHENTICATED_USER)
    public ResponseEntity<?> getAllTasksForUser(
            @CookieValue("jwt") String token,
            HttpServletRequest request,
            @RequestParam(name = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(name = "size", defaultValue = "5") Integer pageSize,
            @RequestParam(name = "status", required = false) List<String> taskStatuses,
            @RequestParam(name = "category", required = false) List<String> categories,
            @RequestParam(name = "sharedTask", defaultValue = "false") boolean sharedTask
    ) {
        long userId = getUserIdFromRequest(request);
        Pageable pageable = createPageable(sortBy, pageNum, pageSize);
        List<TaskStatus> statusFilters = getStatusFilters(taskStatuses);
        List<Category> categoryFilters = getCategoryFilters(categories);

        if (areFiltersEmpty(statusFilters, categoryFilters)) {
            return ResponseEntity.ok().body(userService.getAllTasks(userId, pageable));
        } else {

            Map<String, List<TaskResponse>> taskMap = new HashMap<>();

            List<TaskResponse> sharedTasksResponse = userService.getSharedTasks(token)
                    .stream()
                    .map(task -> modelMapper.map(task, TaskResponse.class))
                    .collect(Collectors.toList());

            List<TaskResponse> unsharedTasksResponse = userService.getAllTasksWithFiltersApplied(
                    userId, statusFilters, categoryFilters, pageable);

            taskMap.put("Shared", sharedTasksResponse);
            taskMap.put("Unshared", unsharedTasksResponse);

            return ResponseEntity.ok().body(
                    taskMap
            );
        }
    }


//    @GetMapping("/tasks")
//    public SomeData getMethodName(@RequestParam String param) {
//        return new SomeData();
//    }
    
    public void getAllTasksForUser() {

    }



    @GetMapping
    public void showProfile(@CookieValue("jwt") String token) {
        User user = userService.getUserIdByToken(token);

    }

    @PatchMapping()
    public void updateProfile() {

    }


    private boolean areFiltersEmpty(List<TaskStatus> statusFilters, List<Category> categoryFilters) {
        return statusFilters.isEmpty() && categoryFilters.isEmpty();
    }

    private long getUserIdFromRequest(HttpServletRequest request) {
        return userService.getUserIdByToken(request).getUserId();
    }

    private Pageable createPageable(String sortBy, Integer pageNum, Integer pageSize) {
        Sort sort = Sort.by(sortBy);
        return PageRequest.of(pageNum, pageSize, sort);
    }

    private List<TaskStatus> getStatusFilters(List<String> taskStatuses) {
        return taskStatuses != null
                ? taskStatuses.stream().map(String::toUpperCase).map(TaskStatus::valueOf).collect(Collectors.toList())
                : Collections.emptyList();
    }

    private List<Category> getCategoryFilters(List<String> categories) {
        return categories != null
                ? categories.stream().map(String::toUpperCase).map(Category::valueOf).toList()
                : Collections.emptyList();
    }




}
