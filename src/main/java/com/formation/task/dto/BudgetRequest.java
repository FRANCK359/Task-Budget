package com.formation.task.dto;

import lombok.Data;

@Data
public class BudgetRequest {
    private Long userId;
    private Integer year;
    private Integer month;
    private Double amount;
}
