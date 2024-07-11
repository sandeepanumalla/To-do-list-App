package com.example.service.impl;

import com.example.model.Task;
import com.example.model.User;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import com.example.request.TaskShareRequest;
import com.example.request.TaskUnShareRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Slf4j
public class TaskShareExecutor {

    private static final String TASK_NOT_FOUND_ERROR = "Task not found";

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    @Autowired
    public TaskShareExecutor(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public void executeShareTask(TaskShareRequest taskShareRequest) {
        long taskId = taskShareRequest.getTaskId();
        List<String> newTaskMembersEmails = taskShareRequest.getUserEmailSet().stream().toList();
        List<Long> newTaskMemberIds = new ArrayList<>();
        newTaskMembersEmails.forEach(email -> {
            newTaskMemberIds.add(userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException(email + "is not registered with us")).getUserId());
        });
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new NoSuchElementException(TASK_NOT_FOUND_ERROR));
//        List<Long> newTaskMemberIds = taskShareRequest.getUserSet().stream().toList();
        Set<User> newTaskMembers = newTaskMemberIds.stream()
                .map(each -> userRepository
                        .findById(each)
                        .orElseThrow(() ->
                                new NoSuchElementException(String.format("No user with userId: %s found", each))))
                .collect(Collectors.toSet());

        task.getSharedWithUsers().addAll(newTaskMembers);
        taskRepository.save(task);
        log.info("task has been shared");
    }


    public void executeUnshareTask(TaskUnShareRequest taskUnShareRequest) {
        Long taskId = taskUnShareRequest.getTaskId();
        List<Long> unShareUserIds = taskUnShareRequest.getUserToBeUnshared().stream().toList();
        List<User> unShareableUsers = userRepository.findAllById(unShareUserIds);
        Task task = taskRepository.findSharedWithUsersByTaskId(taskId).orElseThrow(() -> new NoSuchElementException("Task doesn't exist"));
        task.getSharedWithUsers().removeAll(unShareableUsers);
        taskRepository.saveAndFlush(task);
    }

}
