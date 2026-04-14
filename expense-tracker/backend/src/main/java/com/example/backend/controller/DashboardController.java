package com.example.backend.controller;

import com.example.backend.dto.ChartBar;
import com.example.backend.entity.Expense;
import com.example.backend.entity.ExpenseService;
import com.example.backend.entity.User;
import com.example.backend.service.ReportService;
import com.example.backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class DashboardController {

    private final UserService userService;
    private final ReportService reportService;
    private final ExpenseService expenseService;

    public DashboardController(UserService userService, ReportService reportService, ExpenseService expenseService) {
        this.userService = userService;
        this.reportService = reportService;
        this.expenseService = expenseService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        Long userId = user.getId();

        Map<Month, Double> monthly = reportService.getMonthlyExpenses(userId);
        List<ChartBar> monthlyBars = monthly.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> new ChartBar(
                        e.getKey().getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        e.getValue()))
                .toList();

        List<Expense> recent = expenseService.getRecentExpenses(userId, 8);

        model.addAttribute("monthlyBars", monthlyBars);
        model.addAttribute("totalSpend", reportService.getTotalExpenses(userId));
        model.addAttribute("categoryBreakdown", reportService.getCategoryBreakdown(userId));
        model.addAttribute("recentExpenses", recent);
        model.addAttribute("currentUsername", user.getUsername());
        return "dashboard";
    }
}
