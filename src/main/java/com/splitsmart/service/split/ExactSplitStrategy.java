package com.splitsmart.service.split;

import com.splitsmart.dto.ExpenseParticipantRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExactSplitStrategy implements SplitStrategy {

    @Override
    public List<Long> calculate(
            long amountPaise,
            List<ExpenseParticipantRequest> participants) {

        List<Long> shares = new ArrayList<>();
        long remaining = amountPaise;

        for (ExpenseParticipantRequest participant : participants) {

            Long share = participant.sharePaise();

            if (share == null || share <= 0) {
                throw new IllegalArgumentException(
                        "Each participant must have a positive exact share"
                );
            }

            if (share > remaining) {
                throw new IllegalArgumentException(
                        "Exact shares cannot exceed the expense amount"
                );
            }

            shares.add(share);
            remaining -= share;
        }

        if (remaining != 0) {
            throw new IllegalArgumentException(
                    "Exact shares must add up to the expense amount"
            );
        }

        return shares;
    }
}