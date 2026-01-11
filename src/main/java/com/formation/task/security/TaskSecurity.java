package com.formation.task.security;

import com.formation.task.entities.Task;
import com.formation.task.services.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
@RequiredArgsConstructor
public class TaskSecurity {

    private final TaskService taskService;

    public boolean isOwner(Long taskId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Task task = taskService.getById(taskId);
        return task.getUser().getId().equals(userId);
    }
}
