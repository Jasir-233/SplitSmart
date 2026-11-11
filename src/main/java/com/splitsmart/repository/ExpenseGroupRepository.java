
package com.splitsmart.repository;

import com.splitsmart.entity.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseGroupRepository
        extends JpaRepository<ExpenseGroup, Long> {
}
