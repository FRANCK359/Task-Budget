package com.formation.task.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@ConditionalOnMissingBean(HuggingFaceAIService.class)
public class DummyAIService implements AIService {

    @Override
    public AnalysisResult analyzeTask(String title, String description) {
        log.debug("Service IA factice - analyse tâche: {}", title);

        // Logique simple de priorisation
        int priority = 50;
        String category = "Normal";
        String advice = "Configurez votre token HuggingFace pour une analyse IA avancée.";

        if (title != null) {
            String titleLower = title.toLowerCase();
            if (titleLower.contains("urgent") || titleLower.contains("important") || titleLower.contains("critique")) {
                priority = 85;
                category = "Urgent";
                advice = "Cette tâche semble urgente, traitez-la rapidement.";
            } else if (titleLower.contains("faible") || titleLower.contains("mineur") || titleLower.contains("optionnel")) {
                priority = 25;
                category = "Faible";
                advice = "Priorité faible, peut être traitée plus tard.";
            } else if (titleLower.contains("moyen") || titleLower.contains("normal")) {
                priority = 60;
                category = "Normal";
                advice = "Priorité moyenne, traitez dans les délais normaux.";
            }
        }

        // Ajustement basé sur la description
        if (description != null && description.length() > 200) {
            priority = Math.min(priority + 10, 95);
            advice = "Tâche complexe nécessitant du temps.";
        }

        return new AnalysisResult(priority, category, advice);
    }

    @Override
    public ExpenseAnalysis analyzeExpense(String description, Double amount) {
        log.debug("Service IA factice - analyse dépense: {}", description);

        String category = "Autre";
        String advice = "Catégorisez manuellement cette dépense.";
        boolean essential = false;

        if (description != null) {
            String descLower = description.toLowerCase();

            if (descLower.contains("nourriture") || descLower.contains("aliment") ||
                    descLower.contains("supermarché") || descLower.contains("épicerie")) {
                category = "Alimentation";
                essential = true;
                advice = "Dépense essentielle pour les besoins de base.";

            } else if (descLower.contains("transport") || descLower.contains("essence") ||
                    descLower.contains("carburant") || descLower.contains("bus") ||
                    descLower.contains("métro")) {
                category = "Transport";
                essential = true;
                advice = "Transport essentiel pour les déplacements.";

            } else if (descLower.contains("loyer") || descLower.contains("logement") ||
                    descLower.contains("hypothèque") || descLower.contains("électricité") ||
                    descLower.contains("eau") || descLower.contains("gaz")) {
                category = "Logement";
                essential = true;
                advice = "Dépense essentielle pour le logement.";

            } else if (descLower.contains("santé") || descLower.contains("médicament") ||
                    descLower.contains("docteur") || descLower.contains("hôpital")) {
                category = "Santé";
                essential = true;
                advice = "Dépense essentielle pour la santé.";

            } else if (descLower.contains("loisir") || descLower.contains("divertissement") ||
                    descLower.contains("cinéma") || descLower.contains("restaurant") ||
                    descLower.contains("vacances")) {
                category = "Loisirs";
                essential = false;
                advice = "Dépense non essentielle, à surveiller.";

            } else if (descLower.contains("éducation") || descLower.contains("livre") ||
                    descLower.contains("formation") || descLower.contains("cours")) {
                category = "Éducation";
                essential = amount != null && amount < 100;
                advice = "Investissement dans l'éducation.";
            }
        }

        // Ajustement basé sur le montant
        if (amount != null && amount > 500) {
            advice += " Montant élevé, à surveiller attentivement.";
        }

        return new ExpenseAnalysis(category, advice, essential);
    }

    @Override
    public String generatePlanning(String tasksJson) {
        log.debug("Service IA factice - génération planning");

        return """
                {
                  "planning": [
                    {
                      "task": "Configurer l'IA HuggingFace",
                      "day": "Aujourd'hui",
                      "reason": "Pour activer l'analyse IA avancée et obtenir des recommandations personnalisées"
                    },
                    {
                      "task": "Catégoriser vos dépenses manuellement",
                      "day": "Cette semaine",
                      "reason": "En attendant la configuration de l'IA, assurez-vous de bien catégoriser vos dépenses"
                    },
                    {
                      "task": "Définir les priorités des tâches",
                      "day": "Chaque jour",
                      "reason": "Examinez régulièrement vos tâches et ajustez leurs priorités selon vos objectifs"
                    }
                  ],
                  "message": "Service IA factice actif. Pour des recommandations plus précises, configurez votre token HuggingFace dans les paramètres.",
                  "recommendations": [
                    "Traitez d'abord les tâches marquées comme 'Urgent'",
                    "Revoyez les dépenses non essentielles chaque semaine",
                    "Planifiez les grosses dépenses à l'avance"
                  ]
                }
                """;
    }
}