package com.example.backend.controller;

import com.example.backend.admin.demo.DemoDataService;
import com.example.backend.admin.demo.ExpenseGenerationScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;

@Controller
@RequestMapping("/admin/demo")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDemoPanelController {

    private final DemoDataService demoDataService;

    public AdminDemoPanelController(DemoDataService demoDataService) {
        this.demoDataService = demoDataService;
    }

    @GetMapping
    public String panel(Model model) {
        model.addAttribute("users", demoDataService.listAllUsersForPicker());
        model.addAttribute("defaultYear", Year.now().getValue());
        model.addAttribute("demoPasswordPlain", DemoDataService.DEMO_USER_PASSWORD_PLAIN);
        return "admin/demo";
    }

    @PostMapping("/dummy-users")
    public String createDummyUsers(
            @RequestParam(defaultValue = "5") int count,
            RedirectAttributes redirectAttributes) {
        int created = demoDataService.createDummyUsers(count);
        redirectAttributes.addFlashAttribute(
                "demoMessage",
                "Created " + created + " dummy user(s). Username prefix: "
                        + DemoDataService.DEMO_USERNAME_PREFIX + " — password: "
                        + DemoDataService.DEMO_USER_PASSWORD_PLAIN);
        return "redirect:/admin/demo";
    }

    @PostMapping("/random-expenses")
    public String randomExpenses(
            @RequestParam ExpenseGenerationScope scope,
            @RequestParam(required = false) Long userId,
            @RequestParam int year,
            @RequestParam int count,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            int added = demoDataService.generateRandomExpenses(
                    scope,
                    userId,
                    authentication.getName(),
                    year,
                    count);
            redirectAttributes.addFlashAttribute("demoMessage", "Inserted " + added + " random expense row(s).");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("demoError", ex.getMessage());
        }
        return "redirect:/admin/demo";
    }

    @PostMapping("/delete-all-expenses")
    public String deleteAllExpenses(RedirectAttributes redirectAttributes) {
        long removed = demoDataService.deleteAllExpenses();
        redirectAttributes.addFlashAttribute("demoMessage", "Deleted all expenses (" + removed + " row(s)).");
        return "redirect:/admin/demo";
    }

    @PostMapping("/delete-demo-users")
    public String deleteDemoUsers(RedirectAttributes redirectAttributes) {
        int removedUsers = demoDataService.deleteDemoUsersAndTheirExpenses();
        redirectAttributes.addFlashAttribute(
                "demoMessage",
                "Removed " + removedUsers + " demo user account(s) (prefix " + DemoDataService.DEMO_USERNAME_PREFIX + ") and their expenses.");
        return "redirect:/admin/demo";
    }
}
