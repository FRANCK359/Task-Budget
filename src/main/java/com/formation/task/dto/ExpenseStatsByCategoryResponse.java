package com.formation.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseStatsByCategoryResponse {
    private String category;
    private Double total;
}
