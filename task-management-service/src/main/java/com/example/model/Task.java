package com.example.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import java.time.LocalDateTime;

import java.util.List;
@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class Task implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @CreatedDate
    private LocalDateTime creationDate;

    private LocalDate dueDate;

    private LocalDateTime completionDate;

    private boolean isImportant;

    private TaskStatus taskStatus;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Nullable
    private CategoryTable category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER
           // , cascade = CascadeType.ALL
    )
    @JoinTable(
            name = "shared_tasks",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> sharedWithUsers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "task_assignments",
     joinColumns = @JoinColumn(name = "task_id"),
     inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedToUsers;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "task")
    private List<Attachment> attachments;

    @OneToOne(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Reminder reminder;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Step> steps;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "my_day_task_id")
    private MyDayTask myDayTask;

    @ManyToMany(fetch = FetchType.LAZY
//            , cascade = CascadeType.ALL
    )
    @JoinTable( name = "my_day_task",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"task_id", "user_id"})}
    )
    private List<User> myDayTasks;

    @Column(name = "is_part_of_my_day")
    private boolean isPartOfMyDay;

    @OneToOne(mappedBy = "task" ,orphanRemoval = true, cascade = CascadeType.ALL)
    private TaskRecurrence taskRecurrence;

    public void setIsImportant(Boolean important) {
        isImportant = important;
    }
}
