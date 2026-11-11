package com.splitsmart.service;

import com.splitsmart.dto.CreateExpenseRequest;
import com.splitsmart.dto.ExpenseParticipantRequest;
import com.splitsmart.dto.ExpenseParticipantResponse;
import com.splitsmart.dto.ExpenseResponse;

import com.splitsmart.entity.*;
import com.splitsmart.repository.*;
import com.splitsmart.service.split.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final ExpenseGroupRepository expenseGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final GroupAuthorizationService groupAuthorizationService;

    private final Map<SplitType, SplitStrategy> strategies;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository expenseSplitRepository,
            ExpenseGroupRepository expenseGroupRepository,
            GroupMemberRepository groupMemberRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService,
            GroupAuthorizationService groupAuthorizationService,
            EqualSplitStrategy equalSplitStrategy,
            ExactSplitStrategy exactSplitStrategy,
            PercentageSplitStrategy percentageSplitStrategy) {

        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.expenseGroupRepository = expenseGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.groupAuthorizationService = groupAuthorizationService;

        this.strategies = Map.of(
                SplitType.EQUAL, equalSplitStrategy,
                SplitType.EXACT, exactSplitStrategy,
                SplitType.PERCENTAGE, percentageSplitStrategy
        );
    }

    // CREATE EXPENSE
    @Transactional
    public ExpenseResponse createExpense(
            Long groupId,
            CreateExpenseRequest request) {

        // Get currently logged-in user
        Long currentUserId = currentUserService
                .getCurrentUser()
                .getId();

        // Find group
        ExpenseGroup group = expenseGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Group not found"
                ));

        // Get selected participants from request
        List<ExpenseParticipantRequest> participants =
                request.participants();

        // Check duplicate participants
        Set<Long> participantIds = new HashSet<>();

        for (ExpenseParticipantRequest participant : participants) {

            if (!participantIds.add(participant.userId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate participant"
                );
            }
        }

        // Collect all required user IDs
        Set<Long> requiredUserIds = new HashSet<>(participantIds);

        requiredUserIds.add(currentUserId);
        requiredUserIds.add(request.paidByUserId());

        // Retrieve users
        Map<Long, User> users = new HashMap<>();

        userRepository.findAllById(requiredUserIds)
                .forEach(user -> users.put(user.getId(), user));

        // Check whether users exist
        for (Long userId : requiredUserIds) {

            if (!users.containsKey(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + userId
                );
            }
        }

        // Check group membership
        for (Long userId : requiredUserIds) {

            boolean isMember =
                    groupMemberRepository.existsByGroup_IdAndUser_Id(
                            groupId, userId
                    );

            if (!isMember) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "User " + userId + " is not a group member"
                );
            }
        }

        // Select split strategy
        SplitStrategy strategy = strategies.get(request.splitType());

        if (strategy == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid split type"
            );
        }

        // Validate percentages
        if (request.splitType() == SplitType.PERCENTAGE) {

            participants.forEach(participant -> {

                if (participant.percentage() == null
                        || participant.percentage().signum() <= 0
                        || participant.percentage().compareTo(
                        new java.math.BigDecimal("100")) > 0) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid participant percentage"
                    );
                }
            });
        }

        // Calculate shares
        List<Long> shares;

        try {
            shares = strategy.calculate(
                    request.amountPaise(),
                    participants
            );

        } catch (IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }

        // Create expense
        Expense expense = new Expense();

        expense.setGroup(group);
        expense.setDescription(request.description());
        expense.setAmountPaise(request.amountPaise());
        expense.setPaidBy(users.get(request.paidByUserId()));
        expense.setSplitType(request.splitType());
        expense.setExpenseDate(request.expenseDate());
        expense.setCreatedBy(users.get(currentUserId));

        Expense savedExpense = expenseRepository.save(expense);

        // Create participant splits
        List<ExpenseSplit> splits = new ArrayList<>();

        for (int i = 0; i < participants.size(); i++) {

            ExpenseSplit split = new ExpenseSplit();

            split.setExpense(savedExpense);
            split.setUser(
                    users.get(participants.get(i).userId())
            );
            split.setSharePaise(shares.get(i));

            splits.add(split);
        }

        // Save all participant splits
        List<ExpenseSplit> savedSplits =
                expenseSplitRepository.saveAll(splits);

        // Return expense with participant details
        return toExpenseResponse(savedExpense, savedSplits);
    }

    // GET ALL EXPENSES OF A GROUP
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getGroupExpenses(Long groupId) {

        // Check group exists
        expenseGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Group not found"
                ));

        // Check logged-in user's membership
        groupAuthorizationService.checkMembership(groupId);

        // Retrieve non-deleted expenses
        List<Expense> expenses =
                expenseRepository
                        .findByGroup_IdAndDeletedFalseOrderByCreatedAtDesc(
                                groupId
                        );

        // If group has no expenses
        if (expenses.isEmpty()) {
            return List.of();
        }

        // Collect all expense IDs
        List<Long> expenseIds = expenses.stream()
                .map(Expense::getId)
                .toList();

        // Retrieve all participant splits for these expenses
        List<ExpenseSplit> allSplits =
                expenseSplitRepository.findByExpense_IdIn(expenseIds);

        // Group participant splits by expense ID
        Map<Long, List<ExpenseSplit>> splitsByExpense =
                new HashMap<>();

        for (ExpenseSplit split : allSplits) {

            Long expenseId = split.getExpense().getId();

            splitsByExpense
                    .computeIfAbsent(expenseId, key -> new ArrayList<>())
                    .add(split);
        }

        // Convert expenses into response DTOs
        return expenses.stream()
                .map(expense -> toExpenseResponse(
                        expense,
                        splitsByExpense.getOrDefault(
                                expense.getId(),
                                List.of()
                        )
                ))
                .toList();
    }

    // CONVERT EXPENSE AND ITS PARTICIPANTS INTO RESPONSE DTO
    private ExpenseResponse toExpenseResponse(
            Expense expense,
            List<ExpenseSplit> splits) {

        List<ExpenseParticipantResponse> participants =
                splits.stream()
                        .map(split -> new ExpenseParticipantResponse(
                                split.getUser().getId(),
                                split.getUser().getName(),
                                split.getSharePaise()
                        ))
                        .toList();

        return new ExpenseResponse(
                expense.getId(),
                expense.getGroup().getId(),
                expense.getDescription(),
                expense.getAmountPaise(),
                expense.getPaidBy().getId(),
                expense.getCreatedBy().getId(),
                expense.getSplitType(),
                expense.getExpenseDate(),
                expense.getCreatedAt(),
                participants
        );
    }
}