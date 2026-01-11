package com.formation.task.mappers;

import com.formation.task.dto.BudgetRequest;
import com.formation.task.dto.BudgetResponse;
import com.formation.task.dto.ExpenseResponse;
import com.formation.task.entities.Expense;
import com.formation.task.entities.MonthlyBudget;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BudgetMapper {

    private final ExpenseMapper expenseMapper;

    public BudgetMapper(ExpenseMapper expenseMapper) {
        this.expenseMapper = expenseMapper;
    }

    public BudgetResponse toResponse(MonthlyBudget budget) {
        if (budget == null) return null;

        BudgetResponse dto = new BudgetResponse();
        dto.setId(budget.getId());
        dto.setYear(budget.getYear());
        dto.setMonth(budget.getMonth());
        dto.setBudgetAmount(budget.getBudgetAmount());

        // calcul automatique
        double total = budget.getExpenses()
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        dto.setTotalSpent(total);
        dto.setRemaining(budget.getBudgetAmount() - total);

        // expenses
        dto.setExpenses(
                budget.getExpenses()
                        .stream()
                        .map(expenseMapper::toResponse)
                        .toList()
        );

        return dto;
    }

    public MonthlyBudget toEntity(BudgetRequest req) {
        MonthlyBudget b = new MonthlyBudget();
        b.setYear(req.getYear());
        b.setMonth(req.getMonth());
        b.setBudgetAmount(req.getAmount());
        return b;
    }

    public void updateEntity(MonthlyBudget b, BudgetRequest req) {
        b.setBudgetAmount(req.getAmount());
        b.setYear(req.getYear());
        b.setMonth(req.getMonth());
    }
}
