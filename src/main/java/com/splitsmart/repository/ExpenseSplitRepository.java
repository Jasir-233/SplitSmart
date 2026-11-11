package com.splitsmart.repository;

import com.splitsmart.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ExpenseSplitRepository
        extends JpaRepository<ExpenseSplit, Long> {

    List<ExpenseSplit> findByExpense_Id(Long expenseId);

    List<ExpenseSplit> findByExpense_IdIn(Collection<Long> expenseIds);
}