package com.splitsmart.service.split;

import com.splitsmart.dto.ExpenseParticipantRequest;
import java.util.List;

public interface SplitStrategy {

    List<Long> calculate(
            long amountPaise,
            List<ExpenseParticipantRequest> participants
    );
}