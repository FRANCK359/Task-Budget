package com.formation.task.services;

import com.formation.task.entities.MonthlyBudget;
import com.formation.task.entities.User;
import com.formation.task.repository.MonthlyBudgetRepository;
import com.formation.task.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MonthlyBudgetService {

    private final MonthlyBudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public MonthlyBudgetService(MonthlyBudgetRepository budgetRepository,
                                UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    public MonthlyBudget createOrUpdateBudget(Long userId, int year, int month, double amount) {

        MonthlyBudget budget = budgetRepository
                .findByUserIdAndYearAndMonth(userId, year, month)
                .orElse(new MonthlyBudget());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        budget.setUser(user);
        budget.setYear(year);
        budget.setMonth(month);
        budget.setBudgetAmount(amount);

        return budgetRepository.save(budget);
    }

    public MonthlyBudget getBudgetForMonth(Long userId, int year, int month) {
        return budgetRepository
                .findByUserIdAndYearAndMonth(userId, year, month)
                .orElse(null);
    }

    public double getTotalExpenses(Long budgetId) {
        MonthlyBudget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        return budget.getExpenses()
                .stream()
                .mapToDouble(exp -> exp.getAmount())
                .sum();
    }

    public double getRemaining(Long budgetId) {
        MonthlyBudget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        return budget.getBudgetAmount() - getTotalExpenses(budgetId);
    }
}
