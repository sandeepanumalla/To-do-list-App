package com.example.service;


import com.example.repository.ReminderRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReminderProcessor {

    private final RedisTemplate redisTemplate;

    private final ReminderRepository reminderRepository;

    public ReminderProcessor(RedisTemplate redisTemplate, ReminderRepository reminderRepository) {
        this.redisTemplate = redisTemplate;
        this.reminderRepository = reminderRepository;
    }

//    @Cacheable(value = "reminders")
//    public Task getTaskFromRedis(String taskId) {
//
//    }
}
