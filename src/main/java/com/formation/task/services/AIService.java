package com.formation.task.services;

public interface AIService {

    record AnalysisResult(int priority, String category, String advice) {}

    record ExpenseAnalysis(String category, String advice, boolean essential) {}

    AnalysisResult analyzeTask(String title, String description);

    ExpenseAnalysis analyzeExpense(String description, Double amount);

    String generatePlanning(String tasksJson);
}