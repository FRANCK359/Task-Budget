package com.formation.task.controllers;

import com.formation.task.entities.Expense;
import com.formation.task.services.AIService;
import com.formation.task.services.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/expenses")
@CrossOrigin("*")
public class ExpenseAIController {

    private final AIService aiService;
    private final ExpenseService expenseService;

    public ExpenseAIController(
            AIService aiService,
            ExpenseService expenseService
    ) {
        this.aiService = aiService;
        this.expenseService = expenseService;
    }

    // ---------------------------
    // ANALYSE UNE DÉPENSE VIA IA
    // ---------------------------
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody Expense e) {
        try {
            return ResponseEntity.ok(aiService.analyzeExpense(
                    e.getDescription(),
                    e.getAmount()
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"Erreur lors de l'analyse IA\", \"message\": \"" + ex.getMessage() + "\"}");
        }
    }

    // ---------------------------
    // CATEGORISATION AUTOMATIQUE
    // ---------------------------
    @PostMapping("/{budgetId}/add")
    public ResponseEntity<?> addExpense(
            @PathVariable Long budgetId,
            @RequestBody Expense e
    ) {
        try {
            return ResponseEntity.ok(expenseService.addExpense(budgetId, e));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"Erreur lors de l'ajout de la dépense\", \"message\": \"" + ex.getMessage() + "\"}");
        }
    }
}