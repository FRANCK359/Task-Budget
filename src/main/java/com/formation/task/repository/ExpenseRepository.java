package com.formation.task.repository;

import com.formation.task.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.budget.id = :budgetId GROUP BY e.category")
    List<Object[]> sumByCategory(Long budgetId);

    @Query("SELECT e.date, SUM(e.amount) FROM Expense e WHERE e.budget.id = :budgetId GROUP BY e.date ORDER BY e.date")
    List<Object[]> sumByDay(Long budgetId);

    @Query("""
            SELECT e.date, SUM(e.amount)
            FROM Expense e
            WHERE e.budget.id = :budgetId
            GROUP BY e.date
            ORDER BY e.date
            """)
    List<Object[]> dailyTotals(Long budgetId);
}
