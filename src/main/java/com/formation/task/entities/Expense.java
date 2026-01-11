package com.formation.task.entities;

import com.formation.task.entities.MonthlyBudget;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String category;
    private String description;
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private MonthlyBudget budget;

    // -------------------------
    // 🔥 AJOUT IA
    // -------------------------

    // Catégorie IA (par ex : “Hors budget”, “Essentiel”, “Non essentiel”)
    private String aiCategory;

    // Recommandation IA ("Réduire cette dépense", "Reporter à la fin du mois")
    @Column(columnDefinition = "TEXT")
    private String aiAdvice;

    // 🔥 Le champ qui manquait !
    private boolean essential;
}
