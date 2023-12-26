package com.example.service.impl;

import com.example.model.Task;
import com.example.repository.TaskRepository;
import com.example.request.TaskShareRequest;
import com.example.request.TaskUnShareRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class TaskShareValidator {

    private static final String TASK_NOT_FOUND_ERROR = "Task not found";
    private static final String UNAUTHORIZED_ERROR = "User is not authorized";
    private static final String TASK_ALREADY_SHARED_ERROR = "Some users are already members";
    private static final String SHARE_TO_SELF_ERROR = "You cannot share the task to yourself";
    private static final String NOT_CURRENT_MEMBERS_ERROR = "user is not currently member of the task";

    private final TaskRepository taskRepository;


    @Autowired
    public TaskShareValidator(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    public void validateShareRequest(TaskShareRequest taskShareRequest) {
        long taskId = taskShareRequest.getTaskId();
        List<Long> newTaskMemberIds = taskShareRequest.getUserSet().stream().toList();
        Long taskOwner = taskShareRequest.getTaskOwner().getUserId();

        validateAuthorization(taskId, taskOwner);
        validateMembers(taskId, newTaskMemberIds);
        validateShareToSelf(taskId, newTaskMemberIds);
    }

    private void validateAuthorization(Long taskId, Long userId) {
        if (!isUserAuthorized(taskId, userId)) {
            throw new RuntimeException(UNAUTHORIZED_ERROR);
        }
    }

    private void validateMembers(Long taskId, List<Long> memberIds) {
        if (taskRepository.existsSharedTaskForUsers(memberIds, taskId)) {
            throw new RuntimeException(TASK_ALREADY_SHARED_ERROR);
        }
    }

    private void validateShareToSelf(Long taskId, List<Long> memberIds) {
        if (taskRepository.isOwnerTryingToShareToHimself(taskId, memberIds)) {
            throw new RuntimeException(SHARE_TO_SELF_ERROR);
        }
    }

    private boolean isUserAuthorized(Long taskId, Long userId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException(TASK_NOT_FOUND_ERROR))
                .getOwner().getUserId().equals(userId);
    }

    public void validateUnshareRequest(TaskUnShareRequest taskUnshareRequest) {
        Long taskId = taskUnshareRequest.getTaskId();
        List<Long> unShareUserIds = taskUnshareRequest.getUserToBeUnshared().stream().toList();
        Task task = taskRepository.findSharedWithUsersByTaskId(taskId).orElseThrow(() -> new NoSuchElementException("Task doesn't exist"));
        Long taskOwner = taskUnshareRequest.getTaskOwner().getUserId();

        validateAuthorization(task.getId(), taskOwner);
        validateUsersAreCurrentlyMembers(unShareUserIds, taskId);
    }
    private void validateUsersAreCurrentlyMembers(List<Long> userIds, Long taskId) {
        boolean areUsersCurrentlyMembers = taskRepository.existsSharedTaskForUsers(userIds, taskId);
        if (!areUsersCurrentlyMembers) {
            throw new RuntimeException(NOT_CURRENT_MEMBERS_ERROR);
        }
    }

}
