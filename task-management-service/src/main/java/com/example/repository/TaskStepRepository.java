package com.example.repository;

import com.example.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStepRepository extends JpaRepository<Step, Long> {
}
