package com.example.repository;

import com.example.model.*;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.Optional;
@Repository
public interface TaskRepository extends PagingAndSortingRepository<Task, Long>, JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    List<Task> findAllByCategory(Category category);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.sharedWithUsers WHERE t.id = :taskId")
    Optional<Task> findSharedWithUsersByTaskId(@Param("taskId") Long taskId);


    @Query("SELECT t FROM Task t WHERE t.owner.userId = :ownerId AND t.taskStatus IN :taskStatus")
    Page<Task> findByOwnerIdAndTaskStatus(@Param("ownerId") long ownerId, @Param("taskStatus") List<TaskStatus> taskStatus, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.owner.userId = :ownerId AND t.category IN :categories")
    Page<Task> findByOwnerIdAndCategory(@Param("ownerId") long ownerId, @Param("categories") List<Category> categories, Pageable pageable);
    @Modifying
    @Query("DELETE FROM Task t WHERE t.id = :taskId AND :userId IN (SELECT u.userId FROM t.sharedWithUsers u)")
    void removeOldUsersFromSharedTask(@Param("userId") Long userId, @Param("taskId") Long taskId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM User u JOIN u.sharedTasks t WHERE u.userId = :userId AND t.id = :taskId")
    boolean existsUserWithSharedTask(@Param("userId") Long userId, @Param("taskId") Long taskId);

    @Query("select t from Task t join t.sharedWithUsers sw where sw.userId = :userId")
    List<Task> findSharedTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT t from Task t where t.owner.userId = :ownerId")
    Page<Task> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM User u JOIN u.sharedTasks t WHERE u.userId IN :userIds AND t.id = :taskId")
    boolean existsSharedTaskForUsers(@Param("userIds") List<Long> userIds, Long taskId);


    @Query("SELECT CASE when count(t) > 0 then true else false end from Task t where t.id = :taskId and t.owner.userId IN :userIds")
    boolean isOwnerTryingToShareToHimself(@Param("taskId") Long taskId, @Param("userIds") List<Long> userIds);

    Page<Task> findByOwnerUserIdAndTaskStatusAndIsImportantAndCategoryCategoryNameAndSharedWithUsersNotEmpty(
            Long userId,
            @Nullable TaskStatus status,
            boolean isImportant,
            @Nullable String categoryName,
            Pageable pageable);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM task_master.shared_tasks WHERE task_id = :taskId AND user_id = :userId")
    void deleteFromSharedTasks(@Param("taskId") Long taskId, @Param("userId") Long userId);

    Page<Task> findByOwnerUserIdAndTaskStatusAndIsImportant(long userId, TaskStatus status, boolean isImportant, Pageable pageable);

    Page<Task> findByOwnerUserIdAndIsImportantAndCategoryCategoryName(long userId, boolean isImportant, String category, Pageable pageable);

    Page<Task> findByOwnerUserIdAndTaskStatusAndCategoryCategoryName(long userId, TaskStatus status, String category, Pageable pageable);

    Page<Task> findByOwnerUserIdAndTaskStatus(long userId, TaskStatus status, Pageable pageable);

    Page<Task> findByOwnerUserIdAndCategoryCategoryName(long userId, String category, Pageable pageable);

    Page<Task> findByOwnerUserIdAndIsImportant(long userId, Boolean isImportant, Pageable pageable);
}
