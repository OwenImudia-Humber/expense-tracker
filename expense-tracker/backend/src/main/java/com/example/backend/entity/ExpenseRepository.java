package com.example.backend.entity;

// import com.example.backend.entity.Expense; not used
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserId(Long userId);

    List<Expense> findTop10ByUserIdOrderByDateDesc(Long userId);

    List<Expense> findByUserIdAndCategory(Long userId, ExpenseCategory category);

    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}