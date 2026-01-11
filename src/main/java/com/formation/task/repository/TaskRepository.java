package com.formation.task.repository;

import com.formation.task.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    List<Task> findByCompletedTrue();

    List<Task> findByCompletedFalse();

    List<Task> findByPriorityScoreGreaterThanEqual(Integer threshold);

    List<Task> findByAiCategory(String category);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dateFinEstimee BETWEEN :start AND :end")
    List<Task> findUpcomingTasks(@Param("userId") Long userId,
                                 @Param("start") LocalDate start,
                                 @Param("end") LocalDate end);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.completed = false ORDER BY t.priorityScore DESC NULLS LAST")
    List<Task> findPendingByPriority(@Param("userId") Long userId);

    @Query("""
        SELECT t.aiCategory as category, COUNT(t) as count, AVG(t.priorityScore) as avgPriority
        FROM Task t 
        WHERE t.user.id = :userId 
        GROUP BY t.aiCategory
        ORDER BY COUNT(t) DESC
        """)
    List<Object[]> getCategoryStats(@Param("userId") Long userId);
}