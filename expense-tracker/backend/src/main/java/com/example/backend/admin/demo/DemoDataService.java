package com.example.backend.admin.demo;

import com.example.backend.entity.Expense;
import com.example.backend.entity.ExpenseCategory;
import com.example.backend.entity.ExpenseRepository;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates and removes demo data for the presentation.
 */
@Service
public class DemoDataService {

    public static final String DEMO_USERNAME_PREFIX = "demo_user_";
    public static final String DEMO_USER_PASSWORD_PLAIN = "demo123";

    private static final int MAX_DUMMY_USERS = 25;
    private static final int MAX_EXPENSES_PER_SUBMIT = 500;

    private static final String[] LOREM_DESCRIPTIONS = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.",
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore.",
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt.",
            "Curabitur pretium tincidunt lacus. Nulla gravida orci a odio.",
            "Integer volutpat, eros id convallis iaculis, lacus velit ultricies ligula, sed bibendum enim augue vitae turpis."
    };

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new SecureRandom();

    public DemoDataService(
            UserRepository userRepository,
            ExpenseRepository expenseRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates users with usernames demo_user_&lt;timestamp&gt;_&lt;i&gt; (see {@link #DEMO_USER_PASSWORD_PLAIN}).
     */
    @Transactional
    public int createDummyUsers(int requestedCount) {
        int count = clamp(requestedCount, 1, MAX_DUMMY_USERS);
        long ts = System.currentTimeMillis();
        int created = 0;
        for (int i = 0; i < count; i++) {
            String username = DEMO_USERNAME_PREFIX + ts + "_" + i;
            String email = "demo+" + ts + "_" + i + "@example.invalid";
            if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
                continue;
            }
            User u = new User();
            u.setUsername(username);
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode(DEMO_USER_PASSWORD_PLAIN));
            u.setRole(Role.USER);
            userRepository.save(u);
            created++;
        }
        return created;
    }

    /**
     * @param perUserExpenseCount for ALL_USERS: each user gets this many; for single targets, total for that user
     */
    @Transactional
    public int generateRandomExpenses(
            ExpenseGenerationScope scope,
            Long selectedUserId,
            String currentAdminUsername,
            int year,
            int perUserExpenseCount) {

        int n = clamp(perUserExpenseCount, 1, MAX_EXPENSES_PER_SUBMIT);
        int yearValue = clamp(year, 2000, 2100);
        List<User> targets = resolveTargets(scope, selectedUserId, currentAdminUsername);

        int total = 0;
        for (User user : targets) {
            for (int i = 0; i < n; i++) {
                Expense e = new Expense();
                e.setUser(user);
                e.setAmount(roundMoney(4.99 + random.nextDouble() * 295));
                e.setCategory(ExpenseCategory.values()[random.nextInt(ExpenseCategory.values().length)]);
                e.setDate(randomDateInYear(yearValue));
                e.setDescription(LOREM_DESCRIPTIONS[random.nextInt(LOREM_DESCRIPTIONS.length)]);
                expenseRepository.save(e);
                total++;
            }
        }
        return total;
    }

    @Transactional
    public long deleteAllExpenses() {
        long before = expenseRepository.count();
        expenseRepository.deleteAllInBatch();
        return before;
    }

    /**
     * Deletes users whose username starts with {@value DEMO_USERNAME_PREFIX} and their expenses.
     */
    @Transactional
    public int deleteDemoUsersAndTheirExpenses() {
        List<User> demos = userRepository.findByUsernameStartingWith(DEMO_USERNAME_PREFIX);
        for (User u : demos) {
            expenseRepository.deleteAllExpensesForUser(u.getId());
        }
        userRepository.deleteAllInBatch(demos);
        return demos.size();
    }

    public List<User> listAllUsersForPicker() {
        return userRepository.findAll();
    }

    private List<User> resolveTargets(
            ExpenseGenerationScope scope,
            Long selectedUserId,
            String currentAdminUsername) {

        return switch (scope) {
            case ALL_USERS -> new ArrayList<>(userRepository.findAll());
            case CURRENT_ADMIN -> {
                User u = userRepository.findByUsername(currentAdminUsername).orElseThrow();
                yield List.of(u);
            }
            case SELECTED_USER -> {
                if (selectedUserId == null) {
                    throw new IllegalArgumentException("User id is required for selected scope.");
                }
                User u = userRepository.findById(selectedUserId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + selectedUserId));
                yield List.of(u);
            }
        };
    }

    private LocalDate randomDateInYear(int year) {
        int daysInYear = Year.of(year).length();
        int dayOfYear = 1 + random.nextInt(daysInYear);
        return LocalDate.ofYearDay(year, dayOfYear);
    }

    private static double roundMoney(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
