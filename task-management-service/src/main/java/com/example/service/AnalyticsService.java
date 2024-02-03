package com.example.service;

import com.example.model.Task;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class AnalyticsService {

    private final UserRepository userRepository;

    public AnalyticsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // process the service

    // process(int taskId, int userId)
    //  fetch the user from user repository
    //  fetch all his tasks including shared and unshared
    //  group the tasks based on completion and non completion
    //  completed tasks are success
    //  successRate = completed/total
    //  pending tasks = non completed tasks
    //  update them if required otherwise nothing

    //

//    @RabbitListener(queues = "${rabbitmq.queue.name}", autoStartup = "true")
    public void fetchAllUsers(
//            String receive
    ) {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            process(user);
        }
    }


    void process(User user) {
        List<Task> sharedTasks = user.getSharedTasks();
        List<Task> ownTasks = user.getOwnTasks();
        List<Task> combined = new ArrayList<>();
        combined.addAll(sharedTasks);
        combined.addAll(ownTasks);
        // get total number of tasks
        int total = combined.size();
        // get the tasks that are completed
        int completedTasks = combined.stream()
                .filter(task -> task.getCompletionDate() != null).toList().size();

        // completionRate = completed/total
        int completedRate = completedTasks / total;

        // pending tasks = non completed tasks
        int pendingTasks = combined.stream().filter(task -> task.getCompletionDate() == null).toList().size();
        boolean shouldUpdate = false;
        if(user.getProfile() != null && isCompleteRateDifferent(user, completedRate)) {
            user.getProfile().setSuccessRate(completedRate);
            shouldUpdate = true;
        }
        if(user.getProfile() != null && isPendingRateDifferent(user, pendingTasks)) {
            user.getProfile().setTasksInProgress(pendingTasks);
            shouldUpdate = true;
        }

        if(user.getProfile() != null && isTaskCompletedDifferent(user, completedTasks)) {
            user.getProfile().setTasksCompleted(completedTasks);
            shouldUpdate = true;
        }
        if(shouldUpdate) {
            userRepository.save(user);
        }

    }

    public boolean isCompleteRateDifferent(User user, int completionRate) {
        return (user.getProfile().getSuccessRate() != completionRate);
    }

    public boolean isPendingRateDifferent(User user, int pendingTasks) {
        return user.getProfile().getTasksInProgress() != pendingTasks;
    }

    public boolean isTaskCompletedDifferent(User user, int completedTasks) {
        return user.getProfile().getTasksCompleted() != completedTasks;
    }

}
