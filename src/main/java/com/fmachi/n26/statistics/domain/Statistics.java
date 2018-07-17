package com.fmachi.n26.statistics.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.ZERO;

@Getter
@Builder
@ToString
public class Statistics {
    @Builder.Default
    BigDecimal sum = ZERO;
    @Builder.Default
    BigDecimal avg = ZERO;
    @Builder.Default
    BigDecimal max = ZERO;
    @Builder.Default
    BigDecimal min = ZERO;
    @Builder.Default
    Integer count = 0;
    @Builder.Default
    Long minTimestamp = 0l;


    public Statistics add(Transaction toAdd) {
        int count = this.count + 1;
        BigDecimal sum = sum(toAdd.amount);

        return Statistics.builder()
                .count(count)
                .sum(sum)
                .avg(calculateAvg(count, sum))
                .min(chooseMin(toAdd.amount))
                .max(chooseMax(toAdd.amount))
                .minTimestamp(chooseMinTimestamp(toAdd.timestamp))
                .build();
    }


    private BigDecimal calculateAvg(int count, BigDecimal sum) {
        return sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_EVEN);
    }

    public Statistics combine(Statistics s2) {
        int count = this.count + s2.count;
        BigDecimal sum = sum(s2.sum);
        return Statistics.builder()
                .count(count)
                .sum(sum)
                .avg(calculateAvg(count, sum))
                .min(chooseMin(s2.min))
                .max(chooseMax(s2.max))
                .minTimestamp(chooseMinTimestamp(s2.minTimestamp))
                .build();
    }

    private Long chooseMinTimestamp(Long minTimestamp) {
        if(this.minTimestamp.longValue()==0) {
            return minTimestamp;
        }
        return Math.min(this.minTimestamp,minTimestamp);
    }

    private BigDecimal sum(BigDecimal sum) {
        return this.sum.add(sum).setScale(2);
    }

    private BigDecimal chooseMax(BigDecimal amount) {
        if(max.equals(ZERO)) {
            return amount;
        }
        return max.max(amount);
    }

    private BigDecimal chooseMin(BigDecimal amount) {
        if(min.equals(ZERO)) {
            return amount;
        }
        return min.min(amount);
    }

}
