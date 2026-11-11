package com.splitsmart.service.split;

import com.splitsmart.dto.ExpenseParticipantRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EqualSplitStrategy implements SplitStrategy {

    @Override
    public List<Long> calculate(
            long amountPaise,
            List<ExpenseParticipantRequest> participants) {

        int count = participants.size();

        if (count == 0) {
            throw new IllegalArgumentException(
                    "At least one participant is required"
            );
        }

        long baseShare = amountPaise / count;
        long remainder = amountPaise % count;

        List<Long> shares = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            long share = baseShare + (i < remainder ? 1 : 0);
            shares.add(share);
        }

        return shares;
    }
}