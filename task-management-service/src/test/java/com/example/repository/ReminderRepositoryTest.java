package com.example.repository;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class ReminderRepositoryTest {
    @Test
    public void testSaveReminder() {
        // TODO: Implement test logic for saving a reminder
    }

    @Test
    public void testUpdateReminder() {
        // TODO: Implement test logic for updating a reminder
    }

    @Test
    public void testDeleteReminder() {
        // TODO: Implement test logic for deleting a reminder
    }

    @Test
    public void testFindByReminderOwner() {
        // TODO: Implement test logic for finding reminders by reminder owner
    }

    @Test
    public void testFindByTaskId() {
        // TODO: Implement test logic for finding reminders by task ID
    }

    @Test
    public void testFindDueReminders() {
        // TODO: Implement test logic for finding due reminders
    }
    @Test
    public void testFindAllReminders() {
        // TODO: Implement test logic for finding all reminders
    }


    @Test
    public void testFindByReminderTime() {
        // TODO: Implement test logic for finding reminders by reminder time
    }

    @Test
    public void testCountReminders() {
        // TODO: Implement test logic for counting reminders
    }
    @Test
    public void testFindReminderById() {
        // TODO: Implement test logic for finding a reminder by ID
    }


}
