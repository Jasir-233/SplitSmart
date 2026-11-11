
package com.splitsmart.repository;

import com.splitsmart.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository
        extends JpaRepository<Expense, Long> {

    List<Expense> findByGroup_IdAndDeletedFalseOrderByCreatedAtDesc(
            Long groupId
    );
}
