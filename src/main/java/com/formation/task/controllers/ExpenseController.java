package com.formation.task.controllers;

import com.formation.task.dto.ExpenseRequest;
import com.formation.task.mappers.ExpenseMapper;
import com.formation.task.services.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    public ExpenseController(ExpenseService expenseService, ExpenseMapper expenseMapper) {
        this.expenseService = expenseService;
        this.expenseMapper = expenseMapper;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addExpense(@RequestBody ExpenseRequest req) {

        var expense = expenseMapper.toEntity(req);

        var saved = expenseService.addExpense(req.getBudgetId(), expense);

        return ResponseEntity.ok(expenseMapper.toResponse(saved));
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<?> getExpenses(@PathVariable Long budgetId) {
        return ResponseEntity.ok(
                expenseService.getExpenses(budgetId)
                        .stream()
                        .map(expenseMapper::toResponse)
                        .toList()
        );
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok("Dépense supprimée");
    }
}
