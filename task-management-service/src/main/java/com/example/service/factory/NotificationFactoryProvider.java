package com.example.service.factory;

public class NotificationFactoryProvider {

    public static NotificationFactory getFactory(NotificationType notificationType) {
        return switch(notificationType) {
            case COMMENT -> null;
            case TASK_SHARED -> new TaskSharedNotificationFactory();
            case REMINDER -> new ReminderNotificationFactory();
            case FILE_UPLOAD -> new FileUploadedNotificationFactory();
            default -> throw new IllegalArgumentException("Unknown notification type");
        };
    }
}
