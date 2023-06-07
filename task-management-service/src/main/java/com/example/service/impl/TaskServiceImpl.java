package com.example.service.impl;

import com.example.model.Category;
import com.example.model.Task;
import com.example.repository.TaskRepository;
import com.example.request.TaskRequest;
import com.example.response.TaskResponse;
import com.example.service.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {


    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void setTaskPriority() {

    }

    @Override
    public void updateCategory() {

    }

    @Override
    public TaskResponse createTask(TaskRequest taskRequest) {
        taskRequest.setCategory(Category.valueOf(taskRequest.getCategory().toString()));
        Task task = modelMapper.map(taskRequest, Task.class);
        Task savedTask = taskRepository.save(task);
        TaskResponse taskResponse = modelMapper.map(savedTask, TaskResponse.class);
        return taskResponse;
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return null;
    }

    @Override
    public TaskResponse getTaskById() {
        return null;
    }

    @Override
    public TaskResponse updateTask(long taskId) {
        return null;
    }

    @Override
    public void deleteTask(long taskId) {

    }


}
