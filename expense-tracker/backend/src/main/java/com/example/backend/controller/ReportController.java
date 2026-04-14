package com.example.backend.controller;

import java.time.Month;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.service.ReportService;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // This gets the totals based on user id
    @GetMapping("/total/{id}")
    public Double getTotalExpenses(@PathVariable("id") Long userId) {
        return reportService.getTotalExpenses(userId);
    }

    @GetMapping("/monthly-summary/{id}")
    public Map<Month, Double> getMonthlyExpenses(@PathVariable("id") Long userId) {
        return reportService.getMonthlyExpenses(userId);
    }

    @GetMapping("/category-breakdown/{id}")
    public Map<String, Double> getCategoryBreakdown(@PathVariable("id") Long userId) {
        return reportService.getCategoryBreakdown(userId);
    }
    
}
