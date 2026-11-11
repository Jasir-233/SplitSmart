
package com.splitsmart.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses", indexes = {
        @Index(name = "idx_expenses_group_created",
                columnList = "group_id, created_at")
})
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private ExpenseGroup group;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false)
    private long amountPaise;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paid_by", nullable = false)
    private User paidBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SplitType splitType;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean deleted = false;

    public Expense() {}

    public Long getId() { return id; }
    public ExpenseGroup getGroup() { return group; }
    public String getDescription() { return description; }
    public long getAmountPaise() { return amountPaise; }
    public User getPaidBy() { return paidBy; }
    public SplitType getSplitType() { return splitType; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public User getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isDeleted() { return deleted; }

    public void setGroup(ExpenseGroup group) { this.group = group; }
    public void setDescription(String description) { this.description = description; }
    public void setAmountPaise(long amountPaise) { this.amountPaise = amountPaise; }
    public void setPaidBy(User paidBy) { this.paidBy = paidBy; }
    public void setSplitType(SplitType splitType) { this.splitType = splitType; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
