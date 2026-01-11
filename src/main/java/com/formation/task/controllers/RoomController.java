package com.formation.task.controllers;

import com.formation.task.entities.Room;
import com.formation.task.services.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Room> create(@RequestBody Map<String, Object> body) {
        Long taskId = body.get("taskId") == null ? null : Long.valueOf(body.get("taskId").toString());
        String name = (String) body.getOrDefault("name", "Room");
        boolean persistent = Boolean.parseBoolean(body.getOrDefault("persistent", "false").toString());
        Room r = service.createRoomForTask(taskId, name, persistent);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<Room> get(@PathVariable String publicId) {
        Room r = service.getByPublicId(publicId);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(r);
    }
}
