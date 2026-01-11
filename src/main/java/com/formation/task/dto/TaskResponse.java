package com.formation.task.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskResponse {
    private  Long id;
    private String title;
    private  String description;
    private LocalDate dateDebutEstimee;
    private LocalDate dateFinEstimee;
    private LocalDate dateFinReelle;

    private Long ecart; // en jours ou autre unité de temps
    private Double coutEstime;
    private Double coutReel;
    private Double ecartCout;

    private Long userId;
    private  boolean completed;

}
