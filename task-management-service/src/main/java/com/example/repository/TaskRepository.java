package com.example.repository;

import com.example.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<Task, Long>, JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.sharedWithUsers WHERE t.id = :taskId")
    Optional<Task> findSharedWithUsersByTaskId(@Param("taskId") Long taskId);


    @Query("SELECT t FROM Task t WHERE t.owner.userId = :ownerId AND t.taskStatus IN :taskStatus")
    Page<Task> findByOwnerIdAndTaskStatus(@Param("ownerId") long ownerId, @Param("taskStatus") List<TaskStatus> taskStatus, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.owner.userId = :ownerId AND t.category IN :categories")
    Page<Task> findByOwnerIdAndCategory(@Param("ownerId") long ownerId, @Param("categories") List<Category> categories, Pageable pageable);


    @Query("SELECT t from Task t where t.owner.userId = :ownerId")
    Page<Task> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM User u JOIN u.sharedTasks t WHERE u.userId IN :userIds AND t.id = :taskId")
    boolean existsSharedTaskForUsers(@Param("userIds") List<Long> userIds, Long taskId);


    @Query("SELECT CASE when count(t) > 0 then true else false end from Task t where t.id = :taskId and t.owner.userId IN :userIds")
    boolean isOwnerTryingToShareToHimself(@Param("taskId") Long taskId, @Param("userIds") List<Long> userIds);


//    @Modifying
//    @Query(nativeQuery = true, value = "DELETE FROM task_master.shared_tasks WHERE task_id = :taskId AND user_id = :userId")
//    void deleteFromSharedTasks(@Param("taskId") Long taskId, @Param("userId") Long userId);


    @Query(value = "SELECT t FROM Task t " +
            "LEFT JOIN t.sharedWithUsers s " +
            "WHERE s = :user " +
            "OR (t.owner = :user AND EXISTS (SELECT 1 FROM Task tt WHERE tt = t AND tt.sharedWithUsers IS NOT EMPTY))")
    Page<Task> findSharedTasksByUser(@Param("user") User user, Pageable pageable);


}
