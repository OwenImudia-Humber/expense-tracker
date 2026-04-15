package com.example.backend.entity;

// import com.example.backend.entity.Expense; not used
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserId(Long userId);

    List<Expense> findTop10ByUserIdOrderByDateDesc(Long userId);

    List<Expense> findByUserIdAndCategory(Long userId, ExpenseCategory category);

    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Expense e WHERE e.user.id = :userId")
    void deleteAllExpensesForUser(@Param("userId") Long userId);
}