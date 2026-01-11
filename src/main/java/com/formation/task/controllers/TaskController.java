package com.formation.task.controllers;

import com.formation.task.dto.TaskRequest;
import com.formation.task.dto.TaskResponse;
import com.formation.task.entities.User;
import com.formation.task.mappers.TaskMapper;
import com.formation.task.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "API de gestion des tâches")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    // ----------------------------------------------------------
    // ADMIN → toutes les tâches
    // ----------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(
                taskService.findAll().stream()
                        .map(taskMapper::toResponse)
                        .toList()
        );
    }

    // ----------------------------------------------------------
    // USER → ses tâches
    // ----------------------------------------------------------
    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(
                taskService.findByUser(userId).stream()
                        .map(taskMapper::toResponse)
                        .toList()
        );
    }

    // ----------------------------------------------------------
    // GET ONE → propriétaire ou ADMIN
    // ----------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isOwner(#id, request)")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(taskMapper.toResponse(taskService.getById(id)));
    }

    // ----------------------------------------------------------
    // CREATE → USER connecté
    // ----------------------------------------------------------
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest requestDto,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        User user = taskService.getUserById(userId);

        var task = taskMapper.toEntity(requestDto, user);
        var created = taskService.create(task, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskMapper.toResponse(created));
    }

    // ----------------------------------------------------------
    // UPDATE → propriétaire ou ADMIN
    // ----------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isOwner(#id, request)")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest requestDto,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        User user = taskService.getUserById(userId);

        var task = taskMapper.toEntity(requestDto, user);
        var updated = taskService.update(id, task, userId);

        return ResponseEntity.ok(taskMapper.toResponse(updated));
    }

    // ----------------------------------------------------------
    // DELETE → propriétaire ou ADMIN
    // ----------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isOwner(#id, request)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            HttpServletRequest request) {

        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------
    // COMPLETE → propriétaire
    // ----------------------------------------------------------
    @PreAuthorize("@taskSecurity.isOwner(#id, request)")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        var completed = taskService.markAsCompleted(id, null);
        return ResponseEntity.ok(taskMapper.toResponse(completed));
    }

    // ----------------------------------------------------------
    // STATISTICS → USER
    // ----------------------------------------------------------
    @GetMapping("/statistics")
    public ResponseEntity<TaskService.TaskStatistics> getStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(taskService.getStatistics(userId));
    }
}
