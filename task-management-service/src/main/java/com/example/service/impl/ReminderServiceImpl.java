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
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.*;

import static org.springframework.data.redis.core.ScanOptions.*;


@Service
@Transactional
public class ReminderServiceImpl extends GenericUpdateController<Reminder, Long> implements ReminderService  {

    private final TaskRepository taskRepository;
    private final ReminderRepository reminderRepository;
    private final ModelMapper modelMapper;

    private final NotificationService notificationService;

    private final RedisTemplate<String, Reminder> redisTemplate;

    private static final String REMINDER_CACHE_KEY_PREFIX = "all_reminders";
    private static final String REMINDER_KEY_SET = "reminder_keys";



    @Autowired
    public ReminderServiceImpl(TaskRepository taskRepository, ReminderRepository reminderRepository, ModelMapper modelMapper, NotificationService notificationService, RedisTemplate<String, Reminder> redisTemplate) {
        this.taskRepository = taskRepository;
        this.reminderRepository = reminderRepository;
        this.modelMapper = modelMapper;
        this.notificationService = notificationService;
        this.redisTemplate = redisTemplate;
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
        Reminder savedReminder = reminderRepository.save(reminder);
        task.setReminder(savedReminder);
        taskRepository.save(task);
        appendReminderToCache(reminder);

        return modelMapper.map(reminder, ReminderResponse.class);
    }

    private void appendReminderToCache(Reminder reminder) {
        // Assuming taskId is known, construct the cache key
        String cacheKey = REMINDER_CACHE_KEY_PREFIX + reminder.getTask().getId();

        redisTemplate.opsForValue().set(cacheKey, reminder);
    }



    @Override
    public List<ReminderResponse> getReminders(Long taskId, Long userId)  {
        if(!isUserAuthorizedForReminder(userId, taskId)) {
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
    public ReminderResponse updateReminder(Long taskId, Reminder updatedReminder) {
//        updateField(reminderId, updatedReminder)
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + taskId));

        // Get the reminder associated with the task
        Reminder reminder = task.getReminder();


        reminder.setActive(updatedReminder.isActive());
        reminder.setReminderTime(updatedReminder.getReminderTime());
        reminder.setMessage(updatedReminder.getMessage());

        // Save the updated reminder
        Reminder savedReminder = reminderRepository.save(reminder);
        task.setReminder(savedReminder);

        taskRepository.save(task);
        // Update reminder in the cache
        updateReminderInCache(reminder);

        return modelMapper.map(reminder, ReminderResponse.class);
    }

    private void updateReminderInCache(Reminder updatedReminder) {
        appendReminderToCache(updatedReminder);
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
        evictProcessedRemindersFromCache(taskId);
    }

    @Override
    @Async
    public void sendReminders(Reminder reminder) {


        notificationService.sendNotification("String.valueOf(task.get().getOwner())", "TTmessage", NotificationType.REMINDER);

    }

    public List<Reminder> scanAndRetrieveValues(String pattern) {
        List<Reminder> reminders = new ArrayList<>();

        redisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(scanOptions().match(pattern).build())) {
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    String key = new String(keyBytes, StandardCharsets.UTF_8); // Convert key bytes to String
                    Reminder reminder = redisTemplate.opsForValue().get(key);
                    if (reminder != null) {
                        reminders.add(reminder);
                    }
                }
            }
            return null; // This method doesn't return anything, so return null
        });

        return reminders;
    }

    private Reminder getCachedReminders() {
        // Assuming taskId is known, construct the cache key
        String cacheKey = REMINDER_CACHE_KEY_PREFIX + "10";

        // Retrieve reminders from Redis cache
        Reminder reminder = redisTemplate.opsForValue().get(cacheKey);

        return reminder != null ? reminder : null;
    }

    @Scheduled(fixedRate = 10000)
    public void checkForDueReminders() {

        List<Reminder> scanAndRetrieveValues = scanAndRetrieveValues(REMINDER_CACHE_KEY_PREFIX + "*");

        scanAndRetrieveValues.forEach( reminder -> {
            System.out.println(reminder.getId() + " " +reminder.getMessage() + " " + reminder.getReminderTime());
        });

        scanAndRetrieveValues.forEach(reminder -> {
            sendReminders(reminder);
        });

        System.out.println("reminder watch triggered retrieved" + scanAndRetrieveValues.size());

    }



    private void evictProcessedRemindersFromCache(Long taskId) {
        // Assuming taskId is known, construct the cache key
        String cacheKey = REMINDER_CACHE_KEY_PREFIX + taskId;

        // Evict reminders from Redis cache
        redisTemplate.delete(cacheKey);
    }

}
