package com.fmachi.n26.statistics.domain;

public interface TransactionRepository {

    void addTransaction(Transaction transaction);

    Statistics getStatistics();

    void purge();

    int getTransactionCount();
}

