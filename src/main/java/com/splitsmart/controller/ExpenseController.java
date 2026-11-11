package com.splitsmart.controller;

import com.splitsmart.dto.CreateExpenseRequest;
import com.splitsmart.dto.ExpenseResponse;
import com.splitsmart.service.ExpenseService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // CREATE EXPENSE
    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateExpenseRequest request) {

        ExpenseResponse response =
                expenseService.createExpense(groupId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET ALL EXPENSES OF A GROUP
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getGroupExpenses(
            @PathVariable Long groupId) {

        List<ExpenseResponse> expenses =
                expenseService.getGroupExpenses(groupId);

        return ResponseEntity.ok(expenses);
    }
}