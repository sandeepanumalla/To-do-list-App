package com.example.service.impl;

import com.example.model.*;
import com.example.repository.*;
import com.example.request.TaskRequest;
import com.example.request.TaskUpdateRequest;
import com.example.response.TaskResponse;
import com.example.service.TaskService;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    private final MyDayTaskRepository myDayTaskRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;


    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ModelMapper modelMapper, UserRepository userRepository, CategoryRepository categoryRepository,
                           AttachmentRepository attachmentRepository, MyDayTaskRepository myDayTaskRepository) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.attachmentRepository = attachmentRepository;
        this.myDayTaskRepository = myDayTaskRepository;
    }


    @Override
    public TaskResponse createTask(User owner, TaskRequest taskRequest) throws Exception {
        taskRequest.setCategory(Category.valueOf(taskRequest.getCategory().toString()));
        LocalDate dueDate = taskRequest.getDueDate();
//        if(isValidDate(dueDate) && dueDate.isBefore(LocalDate.now())) {
//            throw new RuntimeException("due date cannot set in past");
//        } else {
            Task task = modelMapper.map(taskRequest, Task.class);
            task.setOwner(owner);
            task.setId(null);
            task.setCreationDate(LocalDateTime.now());
            Task savedTask = taskRepository.save(task);
            return modelMapper.map(savedTask, TaskResponse.class);
//        }
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
//    @CacheEvict(value = "tasks", key = "{#userId, #pageable.pageNumber, #pageable.pageSize}")
    public void deleteTask(long taskId) {
        if(!taskRepository.existsById(taskId)) {
            throw new NoSuchElementException("task with given id does not exist");
        }
//        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalStateException("No task found with given"));
//        List<Attachment> attachments = task.getAttachments();
//        task.setAttachments(null); // Disassociate attachments from task
//        for (Attachment attachment : attachments) {
//            attachment.setTask(null); // Disassociate attachment from task
//            attachmentRepository.delete(attachment); // Delete attachment
//        }
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
    public Map<String, List<Long>> taskSummary(User user) {
        Map<String, List<Long>> taskSummary = new HashMap<>();
        Page<Task> tasks = taskRepository.findByOwnerId(user.getUserId(), Pageable.unpaged());

        List<Long> importantTasks = tasks.stream().filter(Task::isImportant).mapToLong(Task::getId).boxed().collect(Collectors.toList());
        taskSummary.put("important", importantTasks);

        List<Long> completedTasks = tasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.COMPLETED).mapToLong(Task::getId).boxed().collect(Collectors.toList());
        taskSummary.put("completed", completedTasks);

        LocalDate today = LocalDate.now();
        long todayTasksCount = tasks.stream()
                .filter(task -> task.getDueDate() != null && task.getDueDate().equals(LocalDate.now()))
                .count();

        List<Long> myDayTasks;
        try {
             myDayTasks = fetchMyDayTasksWithoutTaskResponse(user);
        } catch (IllegalStateException exception) {
            myDayTasks = Collections.emptyList();
        }

        taskSummary.put("my-day",  myDayTasks);

        taskSummary.put("all", tasks.stream().mapToLong(Task::getId).boxed().toList());

        List<Long> plannedTasks = tasks.stream().filter(task -> task.getDueDate() != null && task.getDueDate().isAfter(LocalDate.now())).mapToLong(Task::getId).boxed().toList();
        taskSummary.put("planned-tasks", plannedTasks);

        taskSummary.put("assignedTo", Collections.emptyList());

        List<Long> unCategorizedTasks = tasks.stream().filter(task -> task.getTaskStatus() == null).mapToLong(Task::getId).boxed().toList();;
        taskSummary.put("unCategorizedTasks",  unCategorizedTasks);

        return taskSummary;
    }


    @Override
    public List<TaskResponse> getAllTasksDup2(long userId, TaskStatus status, Boolean isImportant, String category, Boolean sharedWith, Pageable pageable) {
        List<Task> sharedTasks = new ArrayList<Task>();
        Specification<Task> specification = ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<Predicate>();


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
                  predicates.add(criteriaBuilder.equal(root.get("owner").get("id"), userId));
//                predicates.add(criteriaBuilder.isMember(criteriaBuilder.literal(userId, Expression.class, Long.class), root.join("sharedWithUsers", JoinType.INNER)));
//                predicates.add(criteriaBuilder.isMember(root.get("sharedWithUsers"), n); // Assuming sharedWithJoin is a Join object
                try {
                   User user = userRepository.findById(userId)
                           .orElseThrow(() -> new Exception("User does not exist"));
                    sharedTasks.addAll(user.getSharedTasks());
                    sharedTasks.addAll(user.getOwnTasks().stream().filter(task -> task.getSharedWithUsers().size() > 0).toList());
                } catch (Exception e) {
                    throw new RuntimeException("error while fetching shared tasks " + e);
                }
            } else if (!sharedWith) {
                predicates.add(criteriaBuilder.equal((root.get("owner").get("id")), userId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        Page<Task> tasks = taskRepository.findAll(specification, pageable);

        List<Task> combinedTasks = new ArrayList<>(tasks.getContent());
        combinedTasks.addAll(sharedTasks);

        return convertTasksToTaskResponses(combinedTasks);
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

    @Override
    public List<TaskResponse> getSharedTask(User user, Pageable pageable) {
        List<Task> sharedTasks = new ArrayList<>();
        sharedTasks.addAll(user.getSharedTasks());
        sharedTasks.addAll(user.getOwnTasks().stream()
                .filter(task -> !task.getSharedWithUsers().isEmpty())
                .collect(Collectors.toList()));

        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
           List<Sort.Order> orders = sort.stream().toList();


        }

        return null;
    }

    @Override
    public void addTaskToMyDay(Long taskId, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + taskId));
        if(!checkTaskOwnership(task, user.getUserId())) {
            throw new IllegalStateException("Task not owned by user: " + taskId);
        }
        task.getMyDayTasks().add(user);
        taskRepository.save(task);
        user.getMyDayTasksList().add(task);
        userRepository.save(user);
//        MyDayTask myDayTask = new MyDayTask();
//        myDayTask.getTasks().add(task);
//        myDayTask.setUser(user);
//        MyDayTask savedMyDayTask = myDayTaskRepository.save(myDayTask);
//        task.setMyDayTask(savedMyDayTask);
//        task.setPartOfMyDay(true);
//        taskRepository.save(task);
    }

    @Scheduled(cron = "0 * * * * *")
    public void MyDayTasksProcessor() {
        log.info("MyDayTasksProcessor triggered");
        List<Task> myDayTasks = taskRepository.findByDueDate(LocalDate.now(), TaskStatus.PENDING);
//        List<Task> myDayTask = tasks.stream().filter(task -> task.getDueDate().isEqual(LocalDate.now())).toList();
        myDayTasks.forEach(task -> addTaskToMyDay(task.getId(), task.getOwner()));
        log.info("MyDayTasksProcessor processed " + myDayTasks.size() + " tasks.");
    }

    @Scheduled(cron = "0 * * * * *")
    public void MyDateTasksRemovalProcessor() {
        log.info("MyDateTasksRemovalProcessor triggered");

        List<Task> tasksForRemoval = taskRepository.findByMyDayTaskIsNotNull(LocalDate.now());
        tasksForRemoval.forEach(task -> addTaskToMyDay(task.getId(), task.getOwner()));
        log.info("MyDateTasksRemovalProcessor removed " + tasksForRemoval.size() + " tasks.");

    }


    public boolean checkTaskOwnership(Task task, Long userId) {
        return task.getOwner().getUserId().equals(userId) || task.getSharedWithUsers().stream().anyMatch(user -> { return user.getUserId().equals(userId);});
    }

    private MyDayTask getMyDayTaskById(Long myDayTaskId) {
        return myDayTaskRepository.findById(myDayTaskId)
                .orElseThrow(() -> new IllegalStateException("MyDayTask not found with ID: " + myDayTaskId));
    }
    @Override
    public void removeTaskFromMyDay(Long taskId, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + taskId));

        if(!checkTaskOwnership(task, user.getUserId())) {
            throw new IllegalStateException("Task not owned by user: " + taskId);
        }

        user.getMyDayTasksList().remove(task);

//        MyDayTask myDayTask = getMyDayTaskById(task.getMyDayTask().getId());
//        user.getMyDayTaskList().remove(myDayTask);
//        userRepository.save(user);
//
//        myDayTask.getTasks().remove(task);
//        myDayTaskRepository.save(myDayTask);
//
//        task.setMyDayTask(null);
//        task.setPartOfMyDay(false);
//        taskRepository.save(task);
        userRepository.save(user);
        task.getMyDayTasks().remove(user);
        taskRepository.save(task);
    }

    @Override
    public List<TaskResponse> fetchMyDayTasks(User user) {
        List<Task> myDayTasks = user.getMyDayTasksList();
        if (myDayTasks == null || myDayTasks.isEmpty()) {
            throw new IllegalStateException("MyDayTask not found for user: " + user.getUserId());
        }

//        taskRepository.findAll(ExampleMatcher.matching().withMatcher("description", match -> match.contains())).

//        List<Task> tasks = new ArrayList<>();
//        for (Task myDayTask : myDayTasks) {
//            List<Task> myDayTaskTasks = myDayTask.getTasks();
//            tasks.addAll(myDayTaskTasks);
//        }
        List<TaskResponse> taskResponses = convertTasksToTaskResponses(myDayTasks);
        return taskResponses;
    }

    public List<Long> fetchMyDayTasksWithoutTaskResponse(User user) {
        List<Task> myDayTasks = user.getMyDayTasksList();
        if (myDayTasks == null || myDayTasks.isEmpty()) {
            throw new IllegalStateException("MyDayTask not found for user: " + user.getUserId());
        }

//        taskRepository.findAll(ExampleMatcher.matching().withMatcher("description", match -> match.contains())).

//        List<Task> tasks = new ArrayList<>();
//        for (MyDayTask myDayTask : myDayTasks) {
//            List<Task> myDayTaskTasks = myDayTask.getTasks();
//            tasks.addAll(myDayTaskTasks);
//        }
        return myDayTasks.stream().mapToLong(Task::getId).boxed().collect(Collectors.toList());
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
