
package com.splitsmart.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_splits", indexes = {
        @Index(name = "idx_splits_expense", columnList = "expense_id"),
        @Index(name = "idx_splits_user", columnList = "user_id")
})
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private long sharePaise;

    public ExpenseSplit() {}

    public Long getId() { return id; }
    public Expense getExpense() { return expense; }
    public User getUser() { return user; }
    public long getSharePaise() { return sharePaise; }

    public void setExpense(Expense expense) { this.expense = expense; }
    public void setUser(User user) { this.user = user; }
    public void setSharePaise(long sharePaise) { this.sharePaise = sharePaise; }
}
