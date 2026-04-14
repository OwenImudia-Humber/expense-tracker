package com.example.backend.controller;

import com.example.backend.dto.ExpenseForm;
import com.example.backend.entity.Expense;
import com.example.backend.entity.ExpenseCategory;
import com.example.backend.entity.ExpenseService;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseWebController {

    private final UserService userService;
    private final ExpenseService expenseService;

    public ExpenseWebController(UserService userService, ExpenseService expenseService) {
        this.userService = userService;
        this.expenseService = expenseService;
    }

    @GetMapping
    public String list(
            Authentication authentication,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        User user = requireUser(authentication);
        ExpenseCategory categoryFilter = null;
        if (category != null && !category.isBlank()) {
            try {
                categoryFilter = ExpenseCategory.valueOf(category.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                categoryFilter = null;
            }
        }

        List<Expense> expenses;
        if (categoryFilter != null) {
            expenses = expenseService.getExpensesByCategory(user.getId(), categoryFilter);
        } else if (startDate != null && endDate != null) {
            expenses = expenseService.getExpensesByDateRange(user.getId(), startDate, endDate);
        } else {
            expenses = expenseService.getAllExpenses(user.getId());
        }

        model.addAttribute("expenses", expenses);
        model.addAttribute("categories", ExpenseCategory.values());
        model.addAttribute("filterCategory", categoryFilter);
        model.addAttribute("filterStart", startDate);
        model.addAttribute("filterEnd", endDate);
        return "expenses/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        ExpenseForm form = new ExpenseForm();
        form.setDate(LocalDate.now());
        model.addAttribute("expenseForm", form);
        model.addAttribute("categories", ExpenseCategory.values());
        model.addAttribute("isEdit", false);
        return "expenses/form";
    }

    @PostMapping
    public String create(
            Authentication authentication,
            @Valid @ModelAttribute("expenseForm") ExpenseForm form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", ExpenseCategory.values());
            model.addAttribute("isEdit", false);
            return "expenses/form";
        }

        User user = requireUser(authentication);
        Expense expense = new Expense();
        expense.setAmount(form.getAmount());
        expense.setCategory(form.getCategory());
        expense.setDate(form.getDate());
        expense.setDescription(form.getDescription());
        expense.setUser(user);
        expenseService.createExpense(expense);
        return "redirect:/expenses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Authentication authentication, Model model) {
        User user = requireUser(authentication);
        Expense expense = expenseService.getExpenseOwnedBy(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ExpenseForm form = new ExpenseForm();
        form.setAmount(expense.getAmount());
        form.setCategory(expense.getCategory());
        form.setDate(expense.getDate());
        form.setDescription(expense.getDescription());

        model.addAttribute("expenseForm", form);
        model.addAttribute("categories", ExpenseCategory.values());
        model.addAttribute("expenseId", id);
        model.addAttribute("isEdit", true);
        return "expenses/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @ModelAttribute("expenseForm") ExpenseForm form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", ExpenseCategory.values());
            model.addAttribute("expenseId", id);
            model.addAttribute("isEdit", true);
            return "expenses/form";
        }

        User user = requireUser(authentication);
        expenseService.getExpenseOwnedBy(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Expense patch = new Expense();
        patch.setAmount(form.getAmount());
        patch.setCategory(form.getCategory());
        patch.setDate(form.getDate());
        patch.setDescription(form.getDescription());
        expenseService.updateExpense(id, patch);
        return "redirect:/expenses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        User user = requireUser(authentication);
        Expense expense = expenseService.getExpenseOwnedBy(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        expenseService.deleteExpense(expense.getId());
        return "redirect:/expenses";
    }

    private User requireUser(Authentication authentication) {
        return userService.findOptionalByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
