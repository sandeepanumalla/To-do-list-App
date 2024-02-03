package com.example.service;

import com.example.model.Category;
import com.example.model.Task;
import com.example.model.TaskStatus;
import com.example.model.User;
import com.example.response.TaskResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    public List<TaskResponse> getAllTasks(long userId, Pageable pageable);

    public List<TaskResponse> getAllTasksWithFiltersApplied(long userId,
                                                            List<TaskStatus> statuses,
                                                            List<Category> categories,
                                                            Pageable pageable);

    User getUserIdByToken(HttpServletRequest httpServletRequest);
    User getUserIdByToken(String token);

    List<Task> getSharedTasks(String token);

    public List<TaskResponse>  getAllTasksDup(long userId, TaskStatus status, Boolean isImportant, String category, Pageable pageable);
    public List<TaskResponse>  getAllTasksDup2(long userId, TaskStatus status, Boolean isImportant, String category, Boolean sharedWith, Pageable pageable);

}
