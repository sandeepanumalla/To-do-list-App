package com.example.service;

import com.example.model.Task;
import com.example.request.TaskRequest;
import com.example.response.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest);
    List<TaskResponse> getAllTasks();
    TaskResponse getTaskById();
    TaskResponse updateTask(long taskId);
    void deleteTask(long taskId);
    void setTaskPriority();
    void updateCategory();

    private List<Task> filterByCategory() {
        return null;
    }

    private List<Task> filterByPriority() {
        return null;
    }
}
