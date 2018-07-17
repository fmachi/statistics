package com.fmachi.n26.statistics.persistence.inmemory;

import com.fmachi.n26.statistics.domain.Statistics;
import com.fmachi.n26.statistics.domain.Transaction;
import com.fmachi.n26.statistics.domain.TransactionRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTransactionRepository implements TransactionRepository {

    private List<Transaction> transactions = new LinkedList<>();

    @Override
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public Statistics getStatistics() {
        return transactions.stream()
                .filter(Transaction::notOlderThan60Seconds)
                .reduce(
                        Statistics.builder().build(),
                        Statistics::add,
                        Statistics::combine
                );

    }

    @Override
    public void purge() {
        transactions = transactions.stream()
                .filter(Transaction::notOlderThan60Seconds)
                .collect(Collectors.toList());
    }

    @Override
    public int getTransactionCount() {
        return transactions.size();
    }
}
