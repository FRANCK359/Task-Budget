package com.formation.task.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formation.task.entities.Task;
import com.formation.task.repository.TaskRepository;
import com.formation.task.services.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/tasks")
@CrossOrigin("*")
public class TaskAIController {

    private final AIService aiService;
    private final TaskRepository taskRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public TaskAIController(AIService aiService, TaskRepository repo) {
        this.aiService = aiService;
        this.taskRepository = repo;
    }

    // ---------------------------
    // ANALYSE / PRIORISATION
    // ---------------------------
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody Task t) {
        try {
            return ResponseEntity.ok(aiService.analyzeTask(
                    t.getTitle(),
                    t.getDescription()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"Erreur lors de l'analyse IA\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // ---------------------------
    // RANKING INTELLIGENT
    // ---------------------------
    @GetMapping("/ranking")
    public ResponseEntity<?> smartRanking() {
        try {
            List<Task> tasks = taskRepository.findAll();

            List<Task> sortedTasks = tasks.stream()
                    .sorted((a, b) ->
                            Integer.compare(
                                    b.getPriorityScore() != null ? b.getPriorityScore() : 0,
                                    a.getPriorityScore() != null ? a.getPriorityScore() : 0
                            )
                    )
                    .toList();

            return ResponseEntity.ok(sortedTasks);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Erreur lors du ranking\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // ---------------------------
    // PLANIFICATEUR AUTOMATIQUE
    // ---------------------------
    @GetMapping("/planning")
    public ResponseEntity<?> generatePlanning() {
        try {
            List<Task> tasks = taskRepository.findAll();
            String json = mapper.writeValueAsString(tasks);
            String planning = aiService.generatePlanning(json);
            return ResponseEntity.ok(planning);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Erreur interne\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}