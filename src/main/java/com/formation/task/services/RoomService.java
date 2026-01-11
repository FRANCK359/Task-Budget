package com.formation.task.services;

import com.formation.task.entities.Room;
import com.formation.task.entities.Task;
import com.formation.task.repository.RoomRepository;
import com.formation.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RoomService {

    private final RoomRepository repo;
    private final TaskRepository taskRepository;

    public RoomService(RoomRepository repo, TaskRepository taskRepository) {
        this.repo = repo;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Room createRoomForTask(Long taskId, String name, boolean persistent) {
        Room r = new Room();
        r.setPublicId(UUID.randomUUID().toString());
        r.setName(name);
        r.setPersistent(persistent);
        if (taskId != null) {
            Task t = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found"));
            r.setTask(t);
        }
        return repo.save(r);
    }

    public Room getByPublicId(String publicId) {
        return repo.findByPublicId(publicId).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
