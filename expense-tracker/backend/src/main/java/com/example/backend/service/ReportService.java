// service/ReportService.java

package main.java.com.example.backend.service;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.backend.repository.ExpenseRepository;

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
                    // this gets the month from the date of each expense and uses it as the key for grouping the expenses
                    expense -> expense.getDate().getMonth(),
                    // this sums up the amount of each expense in the same month to get the total expenses for that month
                    Collectors.summingDouble(expense -> expense.getAmount().doubleValue())
                ));
    }

    // Category breakdown
    public Map<String, Double> getCategoryBreakdown(Long userId) {
        return expenseRepository.findByUserId(userId)
        // same thing as above but instead of grouping by month it groups by category to get the total expenses for each category
                .stream()
                .collect(Collectors.groupingBy(
                    // this gets the category of each expense and uses it as the key for grouping the expenses
                    Expense::getCategory,
                    // this sums up the amount of each expense in the same category to get the total expenses for that category
                    Collectors.summingDouble(expense -> expense.getAmount().doubleValue())
                ));
    }
}
