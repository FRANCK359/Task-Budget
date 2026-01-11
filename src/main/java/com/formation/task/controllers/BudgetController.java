package com.formation.task.controllers;
import com.formation.task.dto.BudgetRequest;
import com.formation.task.entities.MonthlyBudget;
import com.formation.task.mappers.BudgetMapper;
import com.formation.task.services.MonthlyBudgetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {
    private final MonthlyBudgetService budgetService;
    private final BudgetMapper budgetMapper;
    public BudgetController(MonthlyBudgetService budgetService, BudgetMapper budgetMapper) {
        this.budgetService = budgetService;
        this.budgetMapper = budgetMapper; }
    @PostMapping("/set")
    public ResponseEntity<?> setBudget( @RequestBody BudgetRequest req, HttpServletRequest request ) {
        Long userId = (Long) request.getAttribute("userId"); MonthlyBudget
                budget = budgetService.createOrUpdateBudget( userId, req.getYear(), req.getMonth(), req.getAmount() );
        return ResponseEntity.ok(budgetMapper.toResponse(budget));
    }
    @GetMapping("/{userId}/{year}/{month}")
    public ResponseEntity<?> getBudget( @PathVariable Long userId, @PathVariable int year, @PathVariable int month) {
        MonthlyBudget budget = budgetService.getBudgetForMonth(userId, year, month);
        return ResponseEntity.ok(budgetMapper.toResponse(budget));
    }
    @GetMapping("/remaining/{budgetId}")
    public ResponseEntity<?> getRemaining(@PathVariable Long budgetId) {
        return ResponseEntity.ok(budgetService.getRemaining(budgetId));
    }
}