package com.fmachi.n26.statistics.persistence.inmemory;

import com.fmachi.n26.statistics.domain.Statistics;
import com.fmachi.n26.statistics.domain.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ThreadSafeTransactionRepositoryTest {

    ThreadSafeTransactionRepository repository;

    @BeforeEach
    void setup() {
        repository = new ThreadSafeTransactionRepository(new InMemoryTransactionRepository(), 20);

        repository.init();
    }

    @AfterEach
    void tearDown() {
        repository.destroy();
    }

    @Test
    void shouldAddOneTransaction() {
        Long timestamp = System.currentTimeMillis();
        BigDecimal amount = new BigDecimal("23.50");
        repository.addTransaction(new Transaction(timestamp, amount));

        waitUntilCountIs(1);

        Statistics statistics = repository.getStatistics();


        assertEquals(amount, statistics.getAvg());
        assertEquals(amount, statistics.getMin());
        assertEquals(amount, statistics.getMax());
        assertEquals(amount, statistics.getSum());
        assertEquals(Integer.valueOf(1), statistics.getCount());
        assertEquals(timestamp, statistics.getMinTimestamp());
    }

    @Test
    void shouldAddManyTransactions() {
        Long timestamp = System.currentTimeMillis();
        IntStream.rangeClosed(1, 10).forEach(
                i -> {
                    BigDecimal amount = new BigDecimal(i + "3.50");
                    repository.addTransaction(new Transaction(timestamp - i * 100, amount));
                }
        );

        waitUntilCountIs(10);

        Statistics statistics = repository.getStatistics();

        assertEquals(new BigDecimal("58.50"), statistics.getAvg());
        assertEquals(new BigDecimal("13.50"), statistics.getMin());
        assertEquals(new BigDecimal("103.50"), statistics.getMax());
        assertEquals(new BigDecimal("585.00"), statistics.getSum());
        assertEquals(Integer.valueOf(10), statistics.getCount());
        assertEquals(timestamp - 1000, statistics.getMinTimestamp().longValue());
    }

    @Test
    void shouldPurgeTransactions() {
        Long timestamp = System.currentTimeMillis();

        IntStream.rangeClosed(1, 4).forEach(
                i -> {
                    BigDecimal amount = new BigDecimal(i + "3.50");
                    repository.addTransaction(new Transaction(timestamp - (60000-i * 120), amount));
                }
        );

        IntStream.rangeClosed(5, 10).forEach(
                i -> {
                    BigDecimal amount = new BigDecimal(i + "3.50");
                    repository.addTransaction(new Transaction(timestamp - (i * 120), amount));
                }
        );

        waitUntilCountIs(10);

        Statistics statistics = repository.getStatistics();

        assertEquals(new BigDecimal("58.50"), statistics.getAvg());
        assertEquals(new BigDecimal("13.50"), statistics.getMin());
        assertEquals(new BigDecimal("103.50"), statistics.getMax());
        assertEquals(new BigDecimal("585.00"), statistics.getSum());
        assertEquals(Integer.valueOf(10), statistics.getCount());
        assertEquals(10, repository.getTransactionCount());
        assertEquals(timestamp - (60000-120), statistics.getMinTimestamp().longValue());

        sleep(500);

        assertEquals(6, repository.getTransactionCount());

        statistics = repository.getStatistics();

        assertEquals(new BigDecimal("78.50"), statistics.getAvg());
        assertEquals(new BigDecimal("53.50"), statistics.getMin());
        assertEquals(new BigDecimal("103.50"), statistics.getMax());
        assertEquals(new BigDecimal("471.00"), statistics.getSum());
        assertEquals(Integer.valueOf(6), statistics.getCount());
        assertEquals(6, repository.getTransactionCount());
        assertEquals(timestamp -1200, statistics.getMinTimestamp().longValue());

    }

    private void waitUntilCountIs(int expectedCound) {
        for (int i = 0; i < 20; i++) {
            if (repository.getTransactionCount() != expectedCound) {
                sleep(20);
            } else {
                return;
            }
        }
        fail("Timeout reached while expecting for count to be equals to " + expectedCound);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }


}