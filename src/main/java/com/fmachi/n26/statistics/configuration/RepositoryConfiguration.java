package com.fmachi.n26.statistics.configuration;

import com.fmachi.n26.statistics.domain.TransactionRepository;
import com.fmachi.n26.statistics.persistence.inmemory.InMemoryTransactionRepository;
import com.fmachi.n26.statistics.persistence.inmemory.ThreadSafeTransactionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfiguration {

    @Value("${statistics.transactions.repository.bufferQueue}")
    private Integer queueSize;

    @Bean("InMemoryTransactionRepository")
    TransactionRepository inMemoryTransactionRepository() {
        return new InMemoryTransactionRepository();
    }

    @Bean("TransactionRepository")
    TransactionRepository transactionRepository(@Qualifier("InMemoryTransactionRepository") TransactionRepository inMemoryTransactionRepository) {
        return new ThreadSafeTransactionRepository(inMemoryTransactionRepository,queueSize);
    }

}
