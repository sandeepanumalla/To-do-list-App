package com.example.response;

import com.example.model.Task;
import java.util.*;

public class TasksWithFilter {
    private List<Task> earlier;
    private List<Task> later;
    private List<Task> today;
    private List<Task> overdue;
    private List<Task> completed;
    private List<Task> all;
    private Map<String, List<Task>> map;
}
