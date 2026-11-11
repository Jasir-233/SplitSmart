
package com.splitsmart.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
@IdClass(GroupMemberId.class)
public class GroupMember {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ExpenseGroup group;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    public GroupMember() {}

    public ExpenseGroup getGroup() { return group; }
    public User getUser() { return user; }
    public LocalDateTime getJoinedAt() { return joinedAt; }

    public void setGroup(ExpenseGroup group) { this.group = group; }
    public void setUser(User user) { this.user = user; }
}
