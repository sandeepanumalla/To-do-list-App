package com.example.repository;

import com.example.model.CategoryTable;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryTableRepository extends JpaRepository<CategoryTable, Long> {
    List<CategoryTable> findByCategoryOwner(User categoryOwner);

    Optional<CategoryTable> findByCategoryName(String categoryName);
}

