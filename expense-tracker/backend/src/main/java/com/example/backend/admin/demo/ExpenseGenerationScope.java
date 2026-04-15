package com.example.backend.admin.demo;

public enum ExpenseGenerationScope {
    /** Every user in the database receives {@code count} expenses each. */
    ALL_USERS,
    /** The logged-in admin user receives {@code count} expenses. */
    CURRENT_ADMIN,
    /** One user chosen in the UI receives {@code count} expenses. */
    SELECTED_USER
}
