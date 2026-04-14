// service/ReportService.java

package com.example.backend.service;
import java.time.Month;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.backend.entity.Expense;
import com.example.backend.entity.ExpenseRepository;

@Service
public class ReportService {
    
    // uses expenseRepository to fetch data for generating reports
    private final ExpenseRepository expenseRepository;

    // constructor injection of the ExpenseRepository
    public ReportService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // Total expenses
    public Double getTotalExpenses(Long userId) {
        return expenseRepository.findByUserId(userId)
        // this fetches the expenses from the user from the list 
        // then it maps the amount of each expense to a double and sums them up to get the total expenses for the user
                .stream()
                .mapToDouble(expense -> expense.getAmount().doubleValue())
                .sum();
    }

    // Monthly expenses
    // this makes a map with key-value pairs where the key is the month and the value is the total expenses for that month
    public Map<Month, Double> getMonthlyExpenses(Long userId){

        return expenseRepository.findByUserId(userId)
        // same thing as above but instead of summing up all the expenses it groups them by month and sums them up for each month to get the total expenses for each month
                .stream()
                .collect(Collectors.groupingBy(
                    expense -> expense.getDate().getMonth(),
                    // this sums up the amount of each expense in the same month to get the total expenses for that month
                    Collectors.summingDouble(expense -> expense.getAmount().doubleValue())
                ));
    }

    // Category breakdown
    public Map<String, Double> getCategoryBreakdown(Long userId) {
        return expenseRepository.findByUserId(userId)
                .stream()
                .collect(Collectors.groupingBy(
                    e -> e.getCategory().name(),
                    Collectors.summingDouble(expense -> expense.getAmount().doubleValue())
                ));
    }

    public Double getTotalExpensesAllUsers() {
        return expenseRepository.findAll().stream()
                .mapToDouble(e -> e.getAmount().doubleValue())
                .sum();
    }

    public Map<Month, Double> getMonthlyExpensesAllUsers() {
        return expenseRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    e -> e.getDate().getMonth(),
                    Collectors.summingDouble(e -> e.getAmount().doubleValue())
                ));
    }

    public Map<String, Double> getCategoryBreakdownAllUsers() {
        return expenseRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    e -> e.getCategory().name(),
                    Collectors.summingDouble(e -> e.getAmount().doubleValue())
                ));
    }
}
