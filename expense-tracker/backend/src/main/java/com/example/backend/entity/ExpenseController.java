package com.example.backend.entity;
import com.example.backend.entity.Expense;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
// import com.example.backend.service.ExpenseService; Expense service not found
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserRepository userRepository;

    // POST /api/expenses
    // Creates a new expense. The request body must include userId.
    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        Optional<User> user = userRepository.findById(expense.getUser().getId());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        expense.setUser(user.get());
        Expense saved = expenseService.createExpense(expense);
        return ResponseEntity.ok(saved);
    }
    // GET /api/expenses?userId=1
    // Returns all expenses for a user.
    // Optional filters: ?category=food  or  ?startDate=2024-01-01&endDate=2024-01-31
    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(
            @RequestParam Long userId,
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Expense> expenses;

        if (category != null) {
            expenses = expenseService.getExpensesByCategory(userId, category);
        } else if (startDate != null && endDate != null) {
            expenses = expenseService.getExpensesByDateRange(userId, startDate, endDate);
        } else {
            expenses = expenseService.getAllExpenses(userId);
        }
        return ResponseEntity.ok(expenses);
    }

    // GET /api/expenses/{id}
    // Returns a single expense by its ID.
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Optional<Expense> expense = expenseService.getExpenseById(id);
        if (expense.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(expense.get());
    }

    // PUT /api/expenses/{id}
    // Updates an existing expense.
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense updatedExpense) {
        Expense result = expenseService.updateExpense(id, updatedExpense);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    // DELETE /api/expenses/{id}
    // Deletes an expense by its ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        boolean deleted = expenseService.deleteExpense(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Expense deleted successfully");
    }
}