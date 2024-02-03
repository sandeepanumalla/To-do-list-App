package com.example.service.impl;

import com.example.model.Category;
import com.example.model.Task;
import com.example.model.TaskStatus;
import com.example.model.User;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import com.example.response.TaskMember;
import com.example.response.TaskResponse;
import com.example.service.UserService;
import com.example.utils.JwtService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final JwtService jwtService;

    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(JwtService jwtService, TaskRepository taskRepository, ModelMapper modelMapper,
                           UserRepository userRepository) {
        this.jwtService = jwtService;
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
//    @Cacheable(value = "tasks", key = "{#userId, #pageableRequest.pageNumber, #pageableRequest.pageSize}")
    public List<TaskResponse> getAllTasks(long userId, Pageable pageableRequest) {

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new RuntimeException("No user found");
        }
        Page<Task> listOfTasks = taskRepository.findByOwnerId(userId, pageableRequest);
        List<TaskResponse> taskResponseList = listOfTasks.stream()
                .map((task) -> TaskResponse.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .category(
//                                task.getCategory()
                        null
                        )
                        .description(task.getDescription())
                        .dueDate(task.getDueDate())
                        .taskStatus(task.getTaskStatus())
                        .completionDate(task.getCompletionDate())
//                        .reminders(task.getReminders())
//                        .priority(task.getPriority())
                        .build())
                .toList();
        for(Task task: listOfTasks) {
            taskResponseList.forEach(taskResponse -> {
                List<String> usernamesList = task.getSharedWithUsers().stream().map(User::getUsername).toList();
//                taskResponse.setMembers(usernamesList);
            });
        }
        return taskResponseList;
    }

    @Override
//    @Cacheable(value = "filteredTasks", key = "{#userId, #statuses, #categories, #pageableRequest.pageNumber, #pageableRequest.pageSize}")
    public List<TaskResponse> getAllTasksWithFiltersApplied(long userId, List<TaskStatus> statuses, List<Category> categories, Pageable pageableRequest) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new RuntimeException("No user found");
        }

//        Page<Task> listOfTasks = taskRepository.findByOptionalCriteria(user.get(), statuses, categories, pageableRequest);
        Page<Task> byOwnerIdAndTaskStatus = taskRepository.findByOwnerIdAndTaskStatus(userId, statuses, pageableRequest);
        Page<Task> byOwnerIdAndCategory = taskRepository.findByOwnerIdAndCategory(userId, categories, pageableRequest);
        Set<Task> listOfTasks = new HashSet<>();
        listOfTasks.addAll(byOwnerIdAndTaskStatus.getContent());
        listOfTasks.addAll(byOwnerIdAndCategory.getContent());
        List<TaskResponse> taskResponseList = listOfTasks.stream()
                .map((task) -> TaskResponse.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .category(
//                                task.getCategory()
                        null
                        )
                        .description(task.getDescription())
                        .dueDate(task.getDueDate())
                        .taskStatus(task.getTaskStatus())
                        .completionDate(task.getCompletionDate())
//                        .reminders(task.getReminders())

//                        .priority(task.getPriority())
                        .build())
                .toList();
        for(Task task: listOfTasks) {
            taskResponseList.forEach(taskResponse -> {
                List<String> usernamesList = task.getSharedWithUsers().stream().map(User::getUsername).toList();
//                taskResponse.setMembers(usernamesList);
            });
        }
        return taskResponseList;
    }

    @Override
    public User getUserIdByToken(HttpServletRequest httpServletRequest) {
        String jwtToken = (String) httpServletRequest.getAttribute("jwtToken");
        return getUser(jwtToken);
    }

    @Override
    public User getUserIdByToken(String token) {
        return getUser(token);
    }

    @Override
    public List<Task> getSharedTasks(String token) {
        return getUser(token).getSharedTasks();
    }

    private User getUser(String token) {
        String usernameOrEmail = jwtService.extractUsername(token);
        Optional<User> user = userRepository.findUserByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if(user.isEmpty()) {
            log.error("user is not found: {} ", user);
            throw new BadCredentialsException("No User found");
        }
        return user.get();
    }

    @Override
    public List<TaskResponse> getAllTasksDup(long userId, TaskStatus status, Boolean isImportant, String category,
                                             Pageable pageable) {

        Page<Task> tasks = null;
        if(category != null && status != null && isImportant != null) {
            tasks = taskRepository.findByOwnerUserIdAndTaskStatusAndIsImportantAndCategoryCategoryNameAndSharedWithUsersNotEmpty(userId, status, isImportant, category, pageable);
        }
        else if(category != null && isImportant != null) { // skip status
            tasks = taskRepository.findByOwnerUserIdAndIsImportantAndCategoryCategoryName(userId, isImportant, category, pageable);
        }
        else if(status != null && isImportant != null) { // skip category
            tasks = taskRepository.findByOwnerUserIdAndTaskStatusAndIsImportant(userId, status, isImportant, pageable);
        }
        else if(category != null && status != null) { //skip isImportant
            tasks = taskRepository.findByOwnerUserIdAndTaskStatusAndCategoryCategoryName(userId, status, category, pageable);
        }
        else if (status != null) { // skip isImportant and category
            tasks = taskRepository.findByOwnerUserIdAndTaskStatus(
                    userId, status, pageable);
        } else if (category != null) { // skip status and isImportant
            tasks = taskRepository.findByOwnerUserIdAndCategoryCategoryName(
                    userId, category, pageable);
        } else if (isImportant != null) { // skip category and status
            tasks = taskRepository.findByOwnerUserIdAndIsImportant(
                    userId, isImportant, pageable);
        }
        else {
            tasks = taskRepository.findByOwnerId(userId, pageable);
        }
        System.out.println(tasks.getContent().size());
//            Page<Task> tasks = taskRepository.findByOwnerUserIdAndTaskStatusAndIsImportantAndCategoryCategoryName(userId, status, isImportant, category, pageable);
//            Page<Task> dup = taskRepository.customFindTasksByCriteria(userId, status, category, pageable);
//        System.out.println(tasks.getSize());
//            System.out.println(dup);
//        System.out.println("dfdf");
        return convertTasksToTaskResponses(tasks.toList());

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


    // Implement the conversion logic if necessary
    private List<TaskResponse> convertTasksToTaskResponses(List<Task> tasks) {
        // Implement the logic to convert Task objects to TaskResponse objects
        return tasks.stream().map((element) -> modelMapper.map(element, TaskResponse.class)).collect(Collectors.toList());
    }
}
