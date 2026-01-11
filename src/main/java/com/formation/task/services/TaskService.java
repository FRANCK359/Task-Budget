package com.formation.task.services;

import com.formation.task.entities.Task;
import com.formation.task.entities.User;
import com.formation.task.exceptions.BusinessException;
import com.formation.task.exceptions.NotFoundException;
import com.formation.task.repository.TaskRepository;
import com.formation.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final AIService aiService;
    private final UserService userService;
    private final UserRepository userRepository;

    /* ===================== CRUD ===================== */

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findByUser(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public List<Task> findCompletedTasks() {
        return taskRepository.findByCompletedTrue();
    }

    public List<Task> findPendingTasks() {
        return taskRepository.findByCompletedFalse();
    }

    public List<Task> findHighPriorityTasks(int threshold) {
        return taskRepository.findByPriorityScoreGreaterThanEqual(threshold);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public Task getById(Long id) {
        return findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Tâche non trouvée avec l'ID: " + id));
    }

    /* ===================== CREATE ===================== */

    @Transactional
    public Task create(Task task, Long userId) {
        validateTask(task);

        User user = userService.getById(userId);
        task.setUser(user);

        // Utilisation du service IA
        try {
            AIService.AnalysisResult analysis = aiService.analyzeTask(
                    task.getTitle(),
                    task.getDescription()
            );

            task.setPriorityScore(analysis.priority());
            task.setAiCategory(analysis.category());
            task.setAiAdvice(analysis.advice());
        } catch (Exception e) {
            log.warn("Erreur lors de l'analyse IA, utilisation des valeurs par défaut", e);
            task.setPriorityScore(50);
            task.setAiCategory("Normal");
            task.setAiAdvice("Analyse manuelle requise");
        }

        calculateTaskMetrics(task);
        return taskRepository.save(task);
    }

    /* ===================== UPDATE ===================== */

    @Transactional
    public Task update(Long id, Task taskDetails, Long userId) {
        Task existingTask = getById(id);

        if (!existingTask.getUser().getId().equals(userId)) {
            throw new BusinessException("Non autorisé");
        }

        existingTask.setTitle(taskDetails.getTitle());
        existingTask.setDescription(taskDetails.getDescription());
        existingTask.setCompleted(taskDetails.isCompleted());
        existingTask.setDateDebutEstimee(taskDetails.getDateDebutEstimee());
        existingTask.setDateFinEstimee(taskDetails.getDateFinEstimee());
        existingTask.setDateFinReelle(taskDetails.getDateFinReelle());
        existingTask.setCoutEstime(taskDetails.getCoutEstime());
        existingTask.setCoutReel(taskDetails.getCoutReel());

        // Ré-analyse IA si changement significatif
        if (hasSignificantChanges(existingTask, taskDetails)) {
            try {
                AIService.AnalysisResult analysis = aiService.analyzeTask(
                        taskDetails.getTitle(),
                        taskDetails.getDescription()
                );
                existingTask.setPriorityScore(analysis.priority());
                existingTask.setAiCategory(analysis.category());
                existingTask.setAiAdvice(analysis.advice());
            } catch (Exception e) {
                log.warn("Erreur lors de la ré-analyse IA", e);
            }
        }

        calculateTaskMetrics(existingTask);
        return taskRepository.save(existingTask);
    }

    /* ===================== DELETE ===================== */

    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Tâche non trouvée avec l'ID: " + id);
        }
        taskRepository.deleteById(id);
        log.info("Tâche {} supprimée", id);
    }

    /* ===================== COMPLETE ===================== */

    @Transactional
    public Task markAsCompleted(Long id, LocalDate actualEndDate) {
        Task task = getById(id);
        task.setCompleted(true);
        task.setDateFinReelle(
                actualEndDate != null ? actualEndDate : LocalDate.now()
        );
        calculateTaskMetrics(task);
        return taskRepository.save(task);
    }

    /* ===================== VALIDATION ===================== */

    private void validateTask(Task task) {
        if (task.getDateDebutEstimee() != null &&
                task.getDateFinEstimee() != null &&
                task.getDateFinEstimee().isBefore(task.getDateDebutEstimee())) {
            throw new BusinessException(
                    "La date de fin estimée ne peut pas être avant la date de début"
            );
        }

        if (task.getCoutEstime() != null && task.getCoutEstime() < 0) {
            throw new BusinessException("Le coût estimé ne peut pas être négatif");
        }
    }

    /* ===================== METRICS ===================== */

    private void calculateTaskMetrics(Task task) {
        // Calcul écart de temps
        if (task.getDateFinReelle() != null && task.getDateFinEstimee() != null) {
            long diff = ChronoUnit.DAYS.between(
                    task.getDateFinEstimee(),
                    task.getDateFinReelle()
            );
            task.setEcart(diff);
        }

        // Calcul écart de coût
        if (task.getCoutReel() != null && task.getCoutEstime() != null) {
            task.setEcartCout(task.getCoutReel() - task.getCoutEstime());
        }
    }

    private boolean hasSignificantChanges(Task oldTask, Task newTask) {
        return (newTask.getTitle() != null && !newTask.getTitle().equals(oldTask.getTitle())) ||
                (newTask.getDescription() != null && !newTask.getDescription().equals(oldTask.getDescription()));
    }

    /* ===================== STATISTICS ===================== */

    public TaskStatistics getStatistics(Long userId) {
        List<Task> userTasks = findByUser(userId);

        long total = userTasks.size();
        long completed = userTasks.stream()
                .filter(Task::isCompleted)
                .count();

        double avgPriority = userTasks.stream()
                .filter(t -> t.getPriorityScore() != null)
                .mapToInt(Task::getPriorityScore)
                .average()
                .orElse(0);

        double totalEstimatedCost = userTasks.stream()
                .filter(t -> t.getCoutEstime() != null)
                .mapToDouble(Task::getCoutEstime)
                .sum();

        double totalActualCost = userTasks.stream()
                .filter(t -> t.getCoutReel() != null)
                .mapToDouble(Task::getCoutReel)
                .sum();

        return new TaskStatistics(
                total,
                completed,
                avgPriority,
                totalEstimatedCost,
                totalActualCost
        );
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /* ===================== RECORD ===================== */

    public record TaskStatistics(
            long totalTasks,
            long completedTasks,
            double averagePriority,
            double totalEstimatedCost,
            double totalActualCost
    ) {}
}