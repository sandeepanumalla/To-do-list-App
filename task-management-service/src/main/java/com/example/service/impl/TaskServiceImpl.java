package com.example.service.impl;

import com.example.model.*;
import com.example.repository.CategoryRepository;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import com.example.request.TaskRequest;
import com.example.request.TaskUpdateRequest;
import com.example.response.TaskResponse;
import com.example.service.TaskService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;


    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ModelMapper modelMapper, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public TaskResponse createTask(User owner, TaskRequest taskRequest) throws Exception {
        taskRequest.setCategory(Category.valueOf(taskRequest.getCategory().toString()));
        LocalDate dueDate = taskRequest.getDueDate();
        if(isValidDate(dueDate) && dueDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("due date cannot set in past");
        } else {
            Task task = modelMapper.map(taskRequest, Task.class);
            task.setOwner(owner);
            task.setId(null);
            task.setCreationDate(LocalDateTime.now());
            Task savedTask = taskRepository.save(task);
            return modelMapper.map(savedTask, TaskResponse.class);
        }
    }

    private boolean isValidDate(LocalDate date) {
        // Check if the date is valid by comparing it with a parsed date string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = date.format(formatter);
        return formattedDate.equals(date.toString());
    }
    @Override
    @Cacheable(value = "taskResponses", key = "#taskId")
    public TaskResponse getTaskById(long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new NoSuchElementException("no task found with " + taskId));
        return modelMapper.map(task, TaskResponse.class);
    }

    @Override
    @CacheEvict(value = "taskResponses", key = "#taskUpdateRequest.getId()") // Assuming TaskResponse has a userId field
    public TaskResponse updateTask(TaskUpdateRequest taskUpdateRequest) {
        if(taskRepository.findById(taskUpdateRequest.getId()).isEmpty()) {
            throw new NoSuchElementException("No task found with given task id");
        }

        Task updatedTask = modelMapper.map(taskUpdateRequest, Task.class);
        Task savedTask = taskRepository.save(updatedTask);
        return modelMapper.map(savedTask, TaskResponse.class);
    }

    @Override
    @CacheEvict(value = "tasks", key = "{#userId, #pageable.pageNumber, #pageable.pageSize}")
    public void deleteTask(long taskId) {
        if(!taskRepository.existsById(taskId)) {
            throw new NoSuchElementException("task with given id does not exist");
        }
         taskRepository.deleteById(taskId);
    }

    @Override
    public void addCategoryToTask(long userId, Long taskId, Long categoryId) throws IllegalAccessException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + taskId));

        CategoryTable category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + categoryId));

        boolean isUserOwner = checkUserOwnershipForTasksAndCategory(userId, taskId, categoryId);
        if(!isUserOwner) {
            throw new IllegalAccessException("User is not authorized to add this category to the task");
        }
        task.setCategory(category); // Assuming Task has a setCategory method
        taskRepository.save(task);
    }

    @Override
    public void removeCategoryFromTask(long userId, Long taskId, Long categoryId) throws IllegalAccessException {
        // Check user ownership
        boolean isUserOwner = checkUserOwnershipForTasksAndCategory(userId, taskId, categoryId);
        if (!isUserOwner) {
            throw new IllegalAccessException("User is not authorized to remove this category from the task");
        }

        // Fetch the task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + taskId));

        // Check if task is associated with the given category
        if (task.getCategory() != null && task.getCategory().getId() == categoryId) {
            // Remove the association
            task.setCategory(null);
            taskRepository.save(task);
        } else {
            throw new NoSuchElementException("Task is not associated with the specified category");
        }
    }

    @Override
//    @Cacheable("taskSummaryCache")
    public Map<String, Integer> taskSummary(Long userId) {
        Map<String, Integer> taskSummary = new HashMap<>();
        Page<Task> tasks = taskRepository.findByOwnerId(userId, Pageable.unpaged());

        long importantTasks = tasks.stream().filter(Task::isImportant).count();
        taskSummary.put("important", (int) importantTasks);

        long completedTasks = tasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.COMPLETED).count();
        taskSummary.put("completed", (int) completedTasks);

        LocalDate today = LocalDate.now();
        long todayTasksCount = tasks.stream()
                .filter(task -> task.getDueDate() != null && task.getDueDate().equals(LocalDate.now()))
                .count();
        taskSummary.put("my-day", (int) todayTasksCount);

        taskSummary.put("all", tasks.stream().toList().size());

        long plannedTasksCount = tasks.stream().filter(task -> task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())).count();
        taskSummary.put("planned-tasks", (int) plannedTasksCount);

        taskSummary.put("assignedTo", 0);

        long unCategorizedTasks = tasks.stream().filter(task -> task.getTaskStatus() == null).count();
        taskSummary.put("unCategorizedTasks", (int) unCategorizedTasks);

        return taskSummary;
    }


    @Override
    public List<TaskResponse> getAllTasksDup2(long userId, TaskStatus status, Boolean isImportant, String category, Boolean sharedWith, Pageable pageable) {
        Specification<Task> specification = ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<Predicate>();

            predicates.add(criteriaBuilder.equal((root.get("owner").get("id")), userId));

            if(status != null) {
                predicates.add(criteriaBuilder.equal((root.get("taskStatus")), status));
            }
            if(category != null) {
                predicates.add(criteriaBuilder.equal((root.get("category")), category));
            }
            if (isImportant != null) {
                predicates.add(criteriaBuilder.equal((root.get("isImportant")), isImportant));
            }
            if(sharedWith) {
                predicates.add(criteriaBuilder.isNotEmpty(root.get("sharedWithUsers")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        Page<Task> tasks = taskRepository.findAll(specification, pageable);
        return convertTasksToTaskResponses(tasks.toList());
    }

    @Override
    public List<TaskResponse> searchTaskByTitle(User user, String keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }
        List<Task> tasks = taskRepository.findAll(((root, query, criteriaBuilder) -> {
            Join<Task, User> ownerJoin = root.join("owner");
            Predicate titlePredicate = criteriaBuilder.like(root.get("title"),  "%" + keywords + "%");
            Predicate userPredicate = criteriaBuilder.equal(ownerJoin.get("userId"), user.getUserId());
            Predicate finalPredicate = criteriaBuilder.and(titlePredicate, userPredicate);
            query.where(finalPredicate);
            return titlePredicate;
        }));
        return convertTasksToTaskResponses(tasks);
    }

    private List<TaskResponse> convertTasksToTaskResponses(List<Task> tasks) {
        // Implement the logic to convert Task objects to TaskResponse objects
        return tasks.stream().map((element) -> modelMapper.map(element, TaskResponse.class)).collect(Collectors.toList());
    }

    public boolean checkUserOwnershipForTasksAndCategory(long userId, long taskId, long categoryId) throws IllegalAccessException {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + taskId));

        CategoryTable category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + categoryId));

        return task.getOwner().getUserId().equals(userId) && category.getCategoryOwner().getUserId().equals(userId);
    }


}
