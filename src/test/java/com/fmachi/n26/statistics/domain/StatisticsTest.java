package com.fmachi.n26.statistics.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.*;

class StatisticsTest {

    @Test
    void shouldBuildAnEmptyStatistics() {
        Statistics statistics = Statistics.builder().build();

        assertEquals(ZERO, statistics.getAvg());
        assertEquals(ZERO, statistics.getMin());
        assertEquals(ZERO, statistics.getMax());
        assertEquals(ZERO, statistics.getSum());
        assertEquals(Integer.valueOf(0), statistics.getCount());
        assertEquals(Long.valueOf(0), statistics.getMinTimestamp());
    }

    @Test
    void shouldAddTransactionToAnEmptyStatistics() {
        Statistics statistics = Statistics.builder().build();

        statistics = statistics.add(new Transaction(11111l, valueOf(13.25)));

        assertEquals(BigDecimal.valueOf(13.25), statistics.getAvg());
        assertEquals(BigDecimal.valueOf(13.25), statistics.getMin());
        assertEquals(BigDecimal.valueOf(13.25), statistics.getMax());
        assertEquals(BigDecimal.valueOf(13.25), statistics.getSum());
        assertEquals(Integer.valueOf(1), statistics.getCount());
        assertEquals(Long.valueOf(11111), statistics.getMinTimestamp());
    }

    @Test
    void shouldAddTransactionToANotEmptyStatistics() {
        Statistics statistics = Statistics.builder().build();

        statistics = statistics.add(new Transaction(88888l, valueOf(13.25)));

        statistics = statistics.add(new Transaction(11111l, valueOf(7.25)));

        assertEquals(BigDecimal.valueOf(10.25), statistics.getAvg());
        assertEquals(BigDecimal.valueOf(7.25), statistics.getMin());
        assertEquals(BigDecimal.valueOf(13.25), statistics.getMax());
        assertEquals(BigDecimal.valueOf(20.50).setScale(2), statistics.getSum());
        assertEquals(Integer.valueOf(2), statistics.getCount());
        assertEquals(Long.valueOf(11111), statistics.getMinTimestamp());
    }

    @Test
    void shouldCombineStatistics() {

        Statistics statistics1 = Statistics.builder()
                .avg(valueOf(22.8))
                .count(3)
                .min(valueOf(7.60))
                .max(valueOf(12.30))
                .minTimestamp(11111l)
                .sum(valueOf(88.23))
                .build();

        Statistics statistics2 = Statistics.builder()
                .avg(valueOf(36.8))
                .count(7)
                .min(valueOf(2.60))
                .max(valueOf(24.30))
                .minTimestamp(13111l)
                .sum(valueOf(188.23))
                .build();

        Statistics combinedStatistics = statistics1.combine(statistics2);

        assertEquals(BigDecimal.valueOf(27.65), combinedStatistics.getAvg());
        assertEquals(BigDecimal.valueOf(2.60), combinedStatistics.getMin());
        assertEquals(BigDecimal.valueOf(24.30), combinedStatistics.getMax());
        assertEquals(BigDecimal.valueOf(276.46), combinedStatistics.getSum());
        assertEquals(Integer.valueOf(10), combinedStatistics.getCount());
        assertEquals(Long.valueOf(11111), combinedStatistics.getMinTimestamp());
    }
}