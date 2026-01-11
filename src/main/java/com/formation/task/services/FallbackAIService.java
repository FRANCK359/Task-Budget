package com.formation.task.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnMissingBean(HuggingFaceAIService.class)
public class FallbackAIService {

    public HuggingFaceAIService.AnalysisResult analyzeTask(String title, String description) {
        log.debug("Utilisation du service IA de secours pour la tâche: {}", title);

        // Logique simple de priorisation basée sur la longueur du titre/description
        int priority = 50;
        String category = "Normal";

        if (title.toLowerCase().contains("urgent") || title.toLowerCase().contains("important")) {
            priority = 80;
            category = "Important";
        }

        if (description != null && description.length() > 100) {
            priority = Math.min(priority + 10, 100);
        }

        String advice = "Priorisez cette tâche manuellement.";

        return new HuggingFaceAIService.AnalysisResult(priority, category, advice);
    }

    public HuggingFaceAIService.ExpenseAnalysis analyzeExpense(String description, Double amount) {
        log.debug("Utilisation du service IA de secours pour la dépense: {}", description);

        String category = "Autre";
        String advice = "Catégorisez manuellement cette dépense.";
        boolean essential = false;

        // Logique simple de catégorisation
        if (description != null) {
            String descLower = description.toLowerCase();
            if (descLower.contains("nourriture") || descLower.contains("aliment") || descLower.contains("restaurant")) {
                category = "Alimentation";
                essential = true;
            } else if (descLower.contains("transport") || descLower.contains("essence") || descLower.contains("bus")) {
                category = "Transport";
                essential = true;
            } else if (descLower.contains("loisir") || descLower.contains("cinéma") || descLower.contains("jeu")) {
                category = "Loisirs";
                essential = false;
            }
        }

        return new HuggingFaceAIService.ExpenseAnalysis(category, advice, essential);
    }
}