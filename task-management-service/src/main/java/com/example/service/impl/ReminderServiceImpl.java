package com.example.service.impl;

import com.example.Controller.GenericUpdateController;
import com.example.model.Reminder;
import com.example.model.Task;
import com.example.repository.ReminderRepository;
import com.example.repository.TaskRepository;
import com.example.request.ReminderRequest;
import com.example.response.ReminderResponse;
import com.example.service.ReminderService;
import com.example.service.factory.NotificationType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ReminderServiceImpl extends GenericUpdateController<Reminder, Long> implements ReminderService  {

    private final TaskRepository taskRepository;
    private final ReminderRepository reminderRepository;
    private final ModelMapper modelMapper;

    private final NotificationService notificationService;

    @Autowired
    public ReminderServiceImpl(TaskRepository taskRepository, ReminderRepository reminderRepository, ModelMapper modelMapper, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.reminderRepository = reminderRepository;
        this.modelMapper = modelMapper;
        this.notificationService = notificationService;
    }
    @Override
    public ReminderResponse setReminder(Long taskId, Long userId, ReminderRequest reminderRequest) {
        Task task = getTaskFromTaskRepository(taskId);
        if (!task.getOwner().getUserId().equals(userId)) {
            // Handle unauthorized access attempt
            try {
                throw new IllegalAccessException("User is not authorized to access these reminders");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        Reminder reminder = modelMapper.map(reminderRequest, Reminder.class);
        reminder.setTask(task);
        reminderRepository.save(reminder);
        return modelMapper.map(reminder, ReminderResponse.class);
    }

    @Override
    public List<ReminderResponse> getReminders(Long taskId, Long userId)  {
        Task task = getTaskFromTaskRepository(taskId);
        if (!task.getOwner().getUserId().equals(userId)) {
            // Handle unauthorized access attempt
            try {
                throw new IllegalAccessException("User is not authorized to access these reminders");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        List<Reminder> reminders = reminderRepository.findByTaskId(taskId);
        return reminders.stream()
                .map(reminder -> modelMapper.map(reminder, ReminderResponse.class))
                .toList();
    }

    private boolean isUserAuthorizedForReminder(Long userId, Long taskId) {
        Task task = getTaskFromTaskRepository(taskId);
        return task.getOwner().getUserId().equals(userId);
    }

    @Override
    public Reminder updateReminder(Long taskId, Long reminderId, Reminder updatedReminder) {
//        updateField(reminderId, updatedReminder)
        return null;
    }

    private Task getTaskFromTaskRepository(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + taskId));
    }

    @Override
    public void deleteReminder(Long taskId, Long reminderId, Long userId) {
        Task task = getTaskFromTaskRepository(taskId);
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new NoSuchElementException("Reminder not found with ID: " + reminderId));

        if (!task.getOwner().getUserId().equals(userId)) {
            // Handle unauthorized access attempt
            try {
                throw new IllegalAccessException("User is not authorized to access these reminders");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        reminderRepository.delete(reminder);
    }

    @Override
    public void sendReminders() {

    }

    @Scheduled(fixedRate = 10000)
    public void checkForDueReminders() {
//       List<Reminder> dueReminders = reminderRepository.findDueReminders(LocalDateTime.now());
//       dueReminders.forEach(this::sendReminders) ;
//        Mono.fromRunnable(() -> {
//
//        }).thenReturn("sent ");
        System.out.println("triggered");
        notificationService.sendNotification("String.valueOf(task.get().getOwner())", "TTmessage", NotificationType.REMINDER);
//        System.out.println(LocalDateTime.now());
    }

    private void sendReminders(Reminder reminder) {
//        Optional<Task> task = taskRepository.findById(reminder.getTask().getId());
//        if(task.isPresent() && task.get().getSharedWithUsers().isEmpty()) {
//            String message = "This is a reminder for " + reminder.getTask().getTitle();
//            notificationService.sendNotification(String.valueOf(task.get().getOwner()), message, NotificationType.REMINDER);
//            reminder.setActive(false);
//        }
    }
}
