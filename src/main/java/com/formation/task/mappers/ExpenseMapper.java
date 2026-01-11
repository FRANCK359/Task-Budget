package com.formation.task.mappers;

import com.formation.task.dto.ExpenseRequest;
import com.formation.task.dto.ExpenseResponse;
import com.formation.task.entities.Expense;
import org.springframework.stereotype.Component;
@Component
public class ExpenseMapper {

    public ExpenseResponse toResponse(Expense e) {
        ExpenseResponse dto = new ExpenseResponse();
        dto.setId(e.getId());
        dto.setDate(e.getDate());
        dto.setCategory(e.getCategory());
        dto.setDescription(e.getDescription());
        dto.setAmount(e.getAmount());
        return dto;
    }

    public Expense toEntity(ExpenseRequest req) {
        Expense e = new Expense();
        e.setAmount(req.getAmount());
        e.setCategory(req.getCategory());
        e.setDate(req.getDate());
        e.setDescription(req.getDescription());
        return e;
    }

    public void updateEntity(Expense e, ExpenseRequest req) {
        e.setAmount(req.getAmount());
        e.setCategory(req.getCategory());
        e.setDate(req.getDate());
        e.setDescription(req.getDescription());
    }
}
