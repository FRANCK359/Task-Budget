package com.formation.task.controllers;

import com.formation.task.services.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense/stats")
public class ExpenseStatsController {

    private final ExpenseService expenseService;

    public ExpenseStatsController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/category/{budgetId}")
    public ResponseEntity<?> statsByCategory(@PathVariable Long budgetId) {
        return ResponseEntity.ok(expenseService.getStatsByCategory(budgetId));
    }

    @GetMapping("/daily/{budgetId}")
    public ResponseEntity<?> statsByDay(@PathVariable Long budgetId) {
        return ResponseEntity.ok(expenseService.getStatsByDay(budgetId));
    }

    @GetMapping("/progression/{budgetId}")
    public ResponseEntity<?> progression(@PathVariable Long budgetId) {
        return ResponseEntity.ok(expenseService.getMonthlyProgression(budgetId));
    }
}
