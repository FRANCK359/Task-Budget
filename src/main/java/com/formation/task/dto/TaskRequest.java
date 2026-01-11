package com.formation.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {
    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    private String description;
    private boolean completed;
    private LocalDate dateDebutEstimee;
    private LocalDate dateFinEstimee;

    private LocalDate dateFinReelle; // peut être null au début
    private Double coutEstime;
    private Double coutReel;
    private Long userId;
}
