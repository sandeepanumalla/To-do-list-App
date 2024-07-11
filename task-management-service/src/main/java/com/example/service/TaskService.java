package com.example.service;

import com.example.model.Task;
import com.example.model.TaskStatus;
import com.example.model.User;
import com.example.request.TaskRequest;
import com.example.request.TaskShareRequest;
import com.example.request.TaskUpdateRequest;
import com.example.response.TaskResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface TaskService {
//    TaskResponse createTask(TaskRequest taskRequest);

    TaskResponse createTask(User owner, TaskRequest taskRequest) throws Exception;

    TaskResponse getTaskById(long taskId);

    TaskResponse updateTask(TaskUpdateRequest taskUpdateRequest);

    void deleteTask(User user, long taskId);

    void addCategoryToTask(long userId, Long taskId, Long categoryId) throws IllegalAccessException;

    void removeCategoryFromTask(long userId, Long taskId, Long categoryId) throws IllegalAccessException;

    Map<String, List<Long>> taskSummary(User user);

    public List<TaskResponse> getAllTasksDup2(long userId, TaskStatus status, Boolean isImportant, String category, Boolean sharedWith, Pageable pageable);

    public List<TaskResponse> searchTaskByTitle(User user, String keywords);

    public List<TaskResponse> getSharedTask(User user, Pageable pageable);

    public void addTaskToMyDay(Long taskId, User user);

    boolean checkTaskOwnership(Task task, Long userId);

    public void removeTaskFromMyDay(Long taskId, User userId);

    public List<TaskResponse> fetchMyDayTasks(User user);

}
