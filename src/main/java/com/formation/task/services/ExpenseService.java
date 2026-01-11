package com.formation.task.services;

import com.formation.task.dto.ExpenseStatsByCategoryResponse;
import com.formation.task.dto.ExpenseStatsDailyResponse;
import com.formation.task.dto.ExpenseStatsProgressionResponse;
import com.formation.task.entities.Expense;
import com.formation.task.repository.ExpenseRepository;
import com.formation.task.repository.MonthlyBudgetRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final MonthlyBudgetRepository budgetRepository;
    private final AIService aiService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            MonthlyBudgetRepository budgetRepository,
            AIService aiService
    ) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.aiService = aiService;
    }

    // -----------------------------
    // ADD EXPENSE + IA
    // -----------------------------
    public Expense addExpense(Long budgetId, Expense expense) {
        expense.setBudget(
                budgetRepository.findById(budgetId)
                        .orElseThrow(() -> new RuntimeException("Budget non trouvé"))
        );

        // Utilisation du service IA
        try {
            AIService.ExpenseAnalysis analysis = aiService.analyzeExpense(
                    expense.getDescription(),
                    expense.getAmount()
            );

            expense.setAiCategory(analysis.category());
            expense.setAiAdvice(analysis.advice());
            expense.setEssential(analysis.essential());
        } catch (Exception e) {
            // Valeurs par défaut en cas d'erreur IA
            expense.setAiCategory("Autre");
            expense.setAiAdvice("Analyse manuelle requise");
            expense.setEssential(false);
        }

        return expenseRepository.save(expense);
    }

    // -----------------------------
    // LIST EXPENSES
    // -----------------------------
    public List<Expense> getExpenses(Long budgetId) {
        return expenseRepository.findAll()
                .stream()
                .filter(e -> e.getBudget() != null && e.getBudget().getId().equals(budgetId))
                .toList();
    }

    // -----------------------------
    // DELETE EXPENSE
    // -----------------------------
    public void deleteExpense(Long expenseId) {
        if (expenseRepository.existsById(expenseId)) {
            expenseRepository.deleteById(expenseId);
        }
    }

    // -----------------------------
    // STATS PAR CATÉGORIE
    // -----------------------------
    public List<ExpenseStatsByCategoryResponse> getStatsByCategory(Long budgetId) {
        List<Object[]> rows = expenseRepository.sumByCategory(budgetId);
        List<ExpenseStatsByCategoryResponse> result = new ArrayList<>();

        for (Object[] r : rows) {
            if (r.length >= 2 && r[0] != null && r[1] != null) {
                result.add(new ExpenseStatsByCategoryResponse(
                        r[0].toString(),
                        ((Number) r[1]).doubleValue()
                ));
            }
        }
        return result;
    }

    // -----------------------------
    // STATS PAR JOUR
    // -----------------------------
    public List<ExpenseStatsDailyResponse> getStatsByDay(Long budgetId) {
        List<Object[]> rows = expenseRepository.sumByDay(budgetId);
        List<ExpenseStatsDailyResponse> result = new ArrayList<>();

        for (Object[] r : rows) {
            if (r.length >= 2 && r[0] != null && r[1] != null) {
                result.add(new ExpenseStatsDailyResponse(
                        (java.time.LocalDate) r[0],
                        ((Number) r[1]).doubleValue()
                ));
            }
        }
        return result;
    }

    // -----------------------------
    // PROGRESSION DU MOIS
    // -----------------------------
    public List<ExpenseStatsProgressionResponse> getMonthlyProgression(Long budgetId) {
        List<Object[]> rows = expenseRepository.dailyTotals(budgetId);
        List<ExpenseStatsProgressionResponse> result = new ArrayList<>();

        double cumulative = 0;

        for (Object[] r : rows) {
            if (r.length >= 2 && r[0] != null && r[1] != null) {
                cumulative += ((Number) r[1]).doubleValue();
                result.add(new ExpenseStatsProgressionResponse(
                        (java.time.LocalDate) r[0],
                        cumulative
                ));
            }
        }
        return result;
    }
}