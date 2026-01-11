package com.formation.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ExpenseStatsDailyResponse {
    private LocalDate date;
    private Double total;
}
