package com.example.service.impl;

import com.example.model.CategoryTable;
import com.example.model.Task;
import com.example.model.User;
import com.example.repository.CategoryTableRepository;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import com.example.request.CategoryRequest;
import com.example.response.CategoryResponse;
import com.example.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryTableRepository categoryTableRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;

    public CategoryServiceImpl(CategoryTableRepository categoryTableRepository, UserRepository userRepository, ModelMapper modelMapper,
                               TaskRepository taskRepository) {
        this.categoryTableRepository = categoryTableRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.taskRepository = taskRepository;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        CategoryTable categoryTable = modelMapper.map(categoryRequest, CategoryTable.class);

        try {
            CategoryTable savedTable = categoryTableRepository.save(categoryTable);
            return modelMapper.map(savedTable, CategoryResponse.class);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = extractErrorMessageFromDataIntegrityViolationException(e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    private String extractErrorMessageFromDataIntegrityViolationException(DataIntegrityViolationException e) {
        if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
            // Further inspection and specific error message construction
            return "Constraint violation: " + cve.getMessage();
        }
        return "Data integrity violation occurred";
    }


    @Override
    public List<CategoryResponse> getAllCategories(User categoryOwner) {
        List<CategoryTable> categoryTables = categoryTableRepository.findByCategoryOwner(categoryOwner);
        return categoryTables.stream().map(each -> modelMapper.map(each, CategoryResponse.class)).toList();
    }

    @Override
    public CategoryTable getCategoryById(Long categoryId) {
        return null;
    }

    private CategoryTable getCategoryByIdAndVerifyOwner(Long categoryId, User owner) throws IllegalAccessException {
        CategoryTable categoryTable = categoryTableRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (!categoryTable.getCategoryOwner().equals(owner)) {
            throw new IllegalAccessException("You are not authorized to perform this action on this category");
        }

        return categoryTable;
    }


    @Override
    public CategoryTable updateCategory(User owner, Long categoryId, CategoryRequest categoryRequest) throws IllegalAccessException {
        CategoryTable categoryTable = getCategoryByIdAndVerifyOwner(categoryId, owner);
        CategoryTable updatedCategoryTable;
        try {
            categoryTable.setCategoryName(categoryRequest.getCategoryName());
            updatedCategoryTable = categoryTableRepository.save(categoryTable);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete the category", e);
        }

        return updatedCategoryTable;
    }

    @Override
    public boolean deleteCategory(User owner, Long categoryId) throws IllegalAccessException {
        // Fetch the category by ID
        CategoryTable categoryTable = getCategoryByIdAndVerifyOwner(categoryId, owner);
        // Attempt to delete the category
        try {
            categoryTableRepository.deleteById(categoryId);
            return true; // Return true if deletion is successful
        } catch (Exception e) {
            // Log the exception and rethrow a custom exception if needed
            throw new RuntimeException("Failed to delete the category", e);
        }
    }

    @Override
    @Transactional
    public void addTask(User user, long categoryId, CategoryRequest categoryRequest) {
        Long taskId = categoryRequest.getTaskId();

        if(taskId != null) {
            CategoryTable categoryTable = categoryTableRepository.findById(categoryId).orElseThrow(() -> new IllegalStateException("Category not found"));
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalStateException("Task with taskId not found"));
            isUserAuthorized(user, categoryTable);
            Set<Task> taskSet = categoryTable.getTasks();
            taskSet.add(task);
            categoryTable.setTasks(taskSet);
            task.setCategory(categoryTable);
            categoryTableRepository.saveAndFlush(categoryTable);
            taskRepository.saveAndFlush(task);
        } else {
            throw new IllegalArgumentException("please provide a taskId to add to category");
        }
    }

    private void isUserAuthorized(User user, CategoryTable categoryTable) {
        if(!user.equals(categoryTable.getCategoryOwner())) {
            throw new IllegalArgumentException("User is not authorized");
        }
    }
}
