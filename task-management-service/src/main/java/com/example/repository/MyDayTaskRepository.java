package com.example.repository;

import com.example.model.MyDayTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyDayTaskRepository extends JpaRepository<MyDayTask, Long> {

}
