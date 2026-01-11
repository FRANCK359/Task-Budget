package com.formation.task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.huggingface.enabled", havingValue = "true", matchIfMissing = false)
public class HuggingFaceAIService implements AIService {

    private final RestTemplate restTemplate;

    @Value("${ai.huggingface.url:https://api-inference.huggingface.co/models}")
    private String baseUrl;

    @Value("${ai.huggingface.model:gpt2}")
    private String model;

    @Value("${ai.huggingface.token:}")
    private String apiToken;

    @Override
    public AnalysisResult analyzeTask(String title, String description) {
        String prompt = """
                Analyse la tâche suivante et retourne STRICTEMENT un JSON :
                {
                  "priority": 1-100,
                  "category": "Urgent | Important | Normal | Faible",
                  "advice": "texte"
                }
                Titre: %s
                Description: %s
                """.formatted(title, description);

        try {
            if (!isTokenValid()) {
                log.debug("Token HuggingFace non configuré");
                return new AnalysisResult(50, "Normal", "Configurez votre token HuggingFace pour l'analyse IA");
            }

            String response = callAI(prompt);
            return parseAnalysisResult(response);
        } catch (Exception e) {
            log.warn("Erreur IA HuggingFace, valeurs par défaut", e);
            return new AnalysisResult(50, "Normal", "Analyse HuggingFace échouée");
        }
    }

    @Override
    public ExpenseAnalysis analyzeExpense(String description, Double amount) {
        String prompt = """
                Analyse la dépense suivante et retourne STRICTEMENT un JSON :
                {
                  "category": "Alimentation | Transport | Logement | Loisirs | Santé | Éducation | Autre",
                  "advice": "texte",
                  "essential": true|false
                }
                Libellé: %s
                Montant: %.2f €
                """.formatted(description, amount);

        try {
            if (!isTokenValid()) {
                return new ExpenseAnalysis("Autre", "Configurez votre token HuggingFace", false);
            }

            String response = callAI(prompt);
            return parseExpenseAnalysis(response);
        } catch (Exception e) {
            return new ExpenseAnalysis("Autre", "Analyse HuggingFace échouée", false);
        }
    }

    @Override
    public String generatePlanning(String tasksJson) {
        String prompt = """
                Génère un planning optimisé au format JSON :
                {
                  "planning": [
                    { "task": "...", "day": "...", "reason": "..." }
                  ]
                }
                Tâches :
                %s
                """.formatted(tasksJson);

        try {
            if (!isTokenValid()) {
                return "{\"planning\": [], \"message\": \"Token HuggingFace non configuré\"}";
            }

            return callAI(prompt);
        } catch (Exception e) {
            return "{\"planning\": [], \"error\": \"Erreur HuggingFace: " + e.getMessage() + "\"}";
        }
    }

    private String callAI(String prompt) {
        String url = baseUrl + "/" + model;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiToken != null && !apiToken.trim().isEmpty()) {
            headers.setBearerAuth(apiToken);
        }

        Map<String, Object> body = Map.of(
                "inputs", prompt,
                "parameters", Map.of(
                        "temperature", 0.3,
                        "max_new_tokens", 400
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                List.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Object firstItem = response.getBody().get(0);
            if (firstItem instanceof Map) {
                Map<?, ?> result = (Map<?, ?>) firstItem;
                Object generatedText = result.get("generated_text");
                return generatedText != null ? generatedText.toString() : "";
            }
        }

        throw new RuntimeException("Erreur Hugging Face API");
    }

    private AnalysisResult parseAnalysisResult(String response) {
        try {
            String priority = extractValue(response, "\"priority\":");
            String category = extractValue(response, "\"category\":");
            String advice = extractValue(response, "\"advice\":");

            int priorityValue = 50;
            try {
                priorityValue = Integer.parseInt(priority.replaceAll("[^0-9]", ""));
                if (priorityValue < 1) priorityValue = 1;
                if (priorityValue > 100) priorityValue = 100;
            } catch (NumberFormatException e) {
                priorityValue = 50;
            }

            return new AnalysisResult(
                    priorityValue,
                    category != null ? category.replace("\"", "").trim() : "Normal",
                    advice != null ? advice.replace("\"", "").trim() : "Analyse par défaut"
            );
        } catch (Exception e) {
            return new AnalysisResult(50, "Normal", "Erreur d'analyse HuggingFace");
        }
    }

    private ExpenseAnalysis parseExpenseAnalysis(String response) {
        try {
            String category = extractValue(response, "\"category\":");
            String advice = extractValue(response, "\"advice\":");
            String essential = extractValue(response, "\"essential\":");

            boolean isEssential = essential != null && essential.contains("true");

            return new ExpenseAnalysis(
                    category != null ? category.replace("\"", "").trim() : "Autre",
                    advice != null ? advice.replace("\"", "").trim() : "Analyse par défaut",
                    isEssential
            );
        } catch (Exception e) {
            return new ExpenseAnalysis("Autre", "Erreur d'analyse HuggingFace", false);
        }
    }

    private String extractValue(String text, String key) {
        if (text == null) return null;

        int start = text.indexOf(key);
        if (start == -1) return null;

        start += key.length();

        int end = text.indexOf(",", start);
        if (end == -1) end = text.indexOf("}", start);
        if (end == -1) return null;

        return text.substring(start, end).trim();
    }

    private boolean isTokenValid() {
        return apiToken != null && !apiToken.trim().isEmpty() && !apiToken.trim().equals("${AI_HUGGINGFACE_TOKEN}");
    }
}