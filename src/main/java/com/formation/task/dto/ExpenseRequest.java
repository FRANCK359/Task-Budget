package com.formation.task.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseRequest {

    private Long budgetId;
    private LocalDate date;
    private String category;
    private String description;
    private Double amount;
}
