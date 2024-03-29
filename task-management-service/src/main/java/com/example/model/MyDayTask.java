package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "my_day_task")
@NoArgsConstructor
@Data
public class MyDayTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

//    public MyDayTask(Task task) {
////        this.tasks.add(task);
//    }

    @OneToMany(mappedBy = "myDayTask", orphanRemoval = false, cascade = CascadeType.ALL)
    private List<Task> tasks = new ArrayList<Task>();


    @ManyToOne
    @JoinColumn(name = "owned_by_user_id")
    private User user;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MyDayTask other = (MyDayTask) obj;
        return id != null && id.equals(other.id); // Assuming 'id' is the unique identifier for MyDayTask
    }

}
