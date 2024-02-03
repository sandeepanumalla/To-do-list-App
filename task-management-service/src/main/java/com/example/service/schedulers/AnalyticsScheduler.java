package com.example.service.schedulers;

import com.example.service.AnalyticsService;
import com.example.service.MessageBroker;
import com.example.service.impl.RabbitMQMessageBroker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AnalyticsScheduler {

    private final MessageBroker messageBroker;

    private final AnalyticsService analyticsService;

    public AnalyticsScheduler(MessageBroker messageBroker, AnalyticsService analyticsService) {
        this.messageBroker = messageBroker;
        this.analyticsService = analyticsService;
    }

    @Scheduled(fixedRate = 60000)
    public void schedule() {
        log.info("Scheduling run");
        analyticsService.fetchAllUsers();
    }

}
