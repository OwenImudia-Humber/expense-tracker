package com.example.backend.entity;

// import com.example.backend.entity.Expense; never used
// import com.example.backend.repository.ExpenseRepository; Expense repository not found
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses(Long userId) {
        return expenseRepository.findByUserId(userId);
    }

    public List<Expense> getRecentExpenses(Long userId, int limit) {
        return expenseRepository.findTop10ByUserIdOrderByDateDesc(userId).stream()
                .limit(limit)
                .toList();
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    // Update an existing expense
    public Expense updateExpense(Long id, Expense updatedExpense) {
        Expense existing = expenseRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }
        existing.setAmount(updatedExpense.getAmount());
        existing.setCategory(updatedExpense.getCategory());
        existing.setDate(updatedExpense.getDate());
        existing.setDescription(updatedExpense.getDescription());
        return expenseRepository.save(existing);
    }

    public boolean deleteExpense(Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Filter expenses by category
    public List<Expense> getExpensesByCategory(Long userId, ExpenseCategory category) {
        return expenseRepository.findByUserIdAndCategory(userId, category);
    }
    public List<Expense> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }

    public Optional<Expense> getExpenseOwnedBy(Long expenseId, Long userId) {
        return expenseRepository.findById(expenseId)
                .filter(e -> e.getUser().getId().equals(userId));
    }
}