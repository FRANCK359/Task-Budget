package com.formation.task.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private boolean completed = false;

    private LocalDate dateDebutEstimee;
    private LocalDate dateFinEstimee;
    private LocalDate dateFinReelle;

    private Long ecart;
    private Double coutEstime;
    private Double coutReel;
    private Double ecartCout;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // -------------------------
    // 🔥 AJOUT IA
    // -------------------------

    // Score IA basé sur l'urgence, l'impact, la charge (0 = faible, 100 = top priorité)
    private Integer priorityScore;

    // Recommandation générée par IA (plan d'action)
    @Column(columnDefinition = "TEXT")
    private String aiAdvice;

    // Catégorie IA ("Urgent", "Normal", "Faible impact", etc.)
    private String aiCategory;
}
