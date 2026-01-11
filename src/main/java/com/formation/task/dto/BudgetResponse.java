package com.formation.task.dto;

import lombok.Data;

import java.util.List;

@Data
public class BudgetResponse {

    private Long id;
    private Integer year;
    private Integer month;
    private Double budgetAmount;
    private Double totalSpent;
    private Double remaining;

    private List<ExpenseResponse> expenses;
}
