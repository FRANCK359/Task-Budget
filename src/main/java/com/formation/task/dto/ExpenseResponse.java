package com.formation.task.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseResponse {

    private Long id;
    private LocalDate date;
    private String category;
    private String description;
    private Double amount;
}
