package com.fmachi.n26.statistics.persistence.inmemory;

import com.fmachi.n26.statistics.domain.Statistics;
import com.fmachi.n26.statistics.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTransactionRepositoryTest {

    InMemoryTransactionRepository inMemoryTransactionRepository = new InMemoryTransactionRepository();

    @Test
    void shouldAddOneTransaction() {
        BigDecimal amount = new BigDecimal("10.00");
        long timestamp = System.currentTimeMillis();
        inMemoryTransactionRepository.addTransaction(new Transaction(timestamp, amount));

        Statistics statistics = inMemoryTransactionRepository.getStatistics();

        assertEquals(new Integer(1),statistics.getCount());
        assertEquals(amount,statistics.getMax());
        assertEquals(amount,statistics.getMin());
        assertEquals(amount,statistics.getAvg());
        assertEquals(amount,statistics.getSum());
        assertEquals(timestamp,statistics.getMinTimestamp().longValue());

        assertEquals(1,inMemoryTransactionRepository.getTransactionCount());

    }

    @Test
    void shouldCombineSomeTransaction() {
        long timestamp = System.currentTimeMillis();
        inMemoryTransactionRepository.addTransaction(new Transaction(timestamp, new BigDecimal("10.00")));
        inMemoryTransactionRepository.addTransaction(new Transaction(timestamp, new BigDecimal("8.00")));
        inMemoryTransactionRepository.addTransaction(new Transaction(timestamp, new BigDecimal("6.00")));
        inMemoryTransactionRepository.addTransaction(new Transaction(timestamp, new BigDecimal("2.00")));

        Statistics statistics = inMemoryTransactionRepository.getStatistics();

        assertEquals(new Integer(4),statistics.getCount());
        assertEquals(new BigDecimal("10.00"),statistics.getMax());
        assertEquals(new BigDecimal("2.00"),statistics.getMin());
        assertEquals(new BigDecimal("6.50"),statistics.getAvg());
        assertEquals(new BigDecimal("26.00"),statistics.getSum());
        assertEquals(timestamp,statistics.getMinTimestamp().longValue());

        assertEquals(4,inMemoryTransactionRepository.getTransactionCount());
    }

    @Test
    void shouldCombineSomeTransactionIgnoringExpired() {
        long timestamp = System.currentTimeMillis();
        givenARepositoryWithSomeExpiredTransactions(timestamp);

        Statistics statistics = inMemoryTransactionRepository.getStatistics();

        assertEquals(new Integer(2),statistics.getCount());
        assertEquals(new BigDecimal("10.00"),statistics.getMax());
        assertEquals(new BigDecimal("6.00"),statistics.getMin());
        assertEquals(new BigDecimal("8.00"),statistics.getAvg());
        assertEquals(new BigDecimal("16.00"),statistics.getSum());
        assertEquals(timestamp,statistics.getMinTimestamp().longValue());

        assertEquals(4,inMemoryTransactionRepository.getTransactionCount());
    }

    @Test
    void shouldPurgeExpiredTransactions() {
        long timestamp = System.currentTimeMillis();
        givenARepositoryWithSomeExpiredTransactions(timestamp);

        assertEquals(4,inMemoryTransactionRepository.getTransactionCount());

        inMemoryTransactionRepository.purge();

        assertEquals(2,inMemoryTransactionRepository.getTransactionCount());

    }

    private void givenARepositoryWithSomeExpiredTransactions(Long timestamp) {
        inMemoryTransactionRepository.addTransaction(new Transaction(timestamp, new BigDecimal("10.00")));
        inMemoryTransactionRepository.addTransaction(new Transaction(timestamp-60001, new BigDecimal("8.00")));
        inMemoryTransactionRepository.addTransaction(new Transaction(timestamp, new BigDecimal("6.00")));
        inMemoryTransactionRepository.addTransaction(new Transaction(timestamp-60002, new BigDecimal("2.00")));

    }

}