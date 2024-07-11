package com.example.repository;

import com.example.model.CategoryTable;
import com.example.model.User;
import com.example.taskmanagementservice.TaskManagementServiceApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import  java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TaskManagementServiceApplication.class)
public class CategoryTableRepositoryTest {
    // TODO: Implement test logic for saving a category

    // write test for fetching a category by name
    @Autowired
    private CategoryTableRepository categoryTableRepository;

    public void testSaveCategory() {

    }

    @Test
    public void testFindByCategoryName_WhenCategoryExists() {
        // Given
        String categoryName = "Test Category";
//        CategoryTable category = new CategoryTable();
//        category.setCategoryName(categoryName);
        // Save the category to the database
//        categoryTableRepository.save(category);

        // When
        Optional<CategoryTable> foundCategory = categoryTableRepository.findByCategoryName(categoryName);

        // Then
        assertTrue(foundCategory.isPresent(), "Category should be found");
//        assertEquals(categoryName, foundCategory.get().getCategoryName(), "Category name should match");
    }
}
