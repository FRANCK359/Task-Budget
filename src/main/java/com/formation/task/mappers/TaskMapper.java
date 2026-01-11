package com.formation.task.mappers;

import com.formation.task.dto.TaskRequest;
import com.formation.task.dto.TaskResponse;
import com.formation.task.entities.Task;
import com.formation.task.entities.User;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request, User user){
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        task.setDateDebutEstimee(request.getDateDebutEstimee());
        task.setDateFinEstimee(request.getDateFinEstimee());
        task.setCoutEstime(request.getCoutEstime());
        task.setUser(user);

        // dateFinReelle et coutReel seront mis à jour plus tard
        task.setDateFinReelle(null);
        task.setCoutReel(null);
        task.setEcart(null);
        task.setEcartCout(null);

        return task;
    }

    public TaskResponse toResponse(Task entity){
        TaskResponse resp= new TaskResponse();
        resp.setId(entity.getId());
        resp.setTitle(entity.getTitle());
        resp.setDescription(entity.getDescription());
        resp.setCompleted(entity.isCompleted());
        resp.setDateDebutEstimee(entity.getDateDebutEstimee());
        resp.setDateFinEstimee(entity.getDateFinEstimee());
        resp.setDateFinReelle(entity.getDateFinReelle());
        resp.setEcart(entity.getEcart());
        resp.setCoutEstime(entity.getCoutEstime());
        resp.setCoutReel(entity.getCoutReel());
        resp.setEcartCout(entity.getEcartCout());
        resp.setUserId(entity.getUser().getId());

        return resp;
    }
}
