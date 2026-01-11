package com.formation.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ExpenseStatsProgressionResponse {
    private LocalDate date;
    private Double cumulative;
}
