package com.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private int sequence;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
