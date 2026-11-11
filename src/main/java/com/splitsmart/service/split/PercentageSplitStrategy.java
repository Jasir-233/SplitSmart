package com.splitsmart.service.split;

import com.splitsmart.dto.ExpenseParticipantRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class PercentageSplitStrategy implements SplitStrategy {

    private record Remainder(int index, BigDecimal fraction) {}

    @Override
    public List<Long> calculate(
            long amountPaise,
            List<ExpenseParticipantRequest> participants) {

        BigDecimal totalPercentage = participants.stream()
                .map(ExpenseParticipantRequest::percentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPercentage.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalArgumentException(
                    "Percentages must add up to 100"
            );
        }

        List<Long> shares = new ArrayList<>();
        List<Remainder> remainders = new ArrayList<>();

        long allocated = 0;

        for (int i = 0; i < participants.size(); i++) {

            BigDecimal exactShare = BigDecimal.valueOf(amountPaise)
                    .multiply(participants.get(i).percentage())
                    .divide(new BigDecimal("100"));

            long baseShare = exactShare
                    .setScale(0, RoundingMode.DOWN)
                    .longValueExact();

            allocated += baseShare;
            shares.add(baseShare);

            BigDecimal fraction = exactShare.subtract(
                    BigDecimal.valueOf(baseShare)
            );

            remainders.add(new Remainder(i, fraction));
        }

        long remaining = amountPaise - allocated;

        remainders.sort(
                Comparator.comparing(Remainder::fraction).reversed()
        );

        for (int i = 0; i < remaining; i++) {
            int index = remainders.get(i).index();
            shares.set(index, shares.get(index) + 1);
        }

        return shares;
    }
}