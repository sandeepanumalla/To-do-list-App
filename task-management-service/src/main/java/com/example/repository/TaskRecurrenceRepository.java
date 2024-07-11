package com.example.repository;

import com.example.model.TaskRecurrence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRecurrenceRepository extends JpaRepository<TaskRecurrence, Long> {
}
