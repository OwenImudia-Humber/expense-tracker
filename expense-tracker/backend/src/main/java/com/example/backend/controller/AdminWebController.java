package com.example.backend.controller;

import com.example.backend.dto.ChartBar;
import com.example.backend.entity.ExpenseRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWebController {

    private final ReportService reportService;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public AdminWebController(
            ReportService reportService,
            UserRepository userRepository,
            ExpenseRepository expenseRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("expenseCount", expenseRepository.count());
        model.addAttribute("totalAll", reportService.getTotalExpensesAllUsers());

        Map<Month, Double> monthly = reportService.getMonthlyExpensesAllUsers();
        List<ChartBar> monthlyBars = monthly.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> new ChartBar(
                        e.getKey().getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        e.getValue()))
                .toList();
        model.addAttribute("monthlyBars", monthlyBars);
        model.addAttribute("categoryBreakdown", reportService.getCategoryBreakdownAllUsers());
        return "admin/dashboard";
    }
}
