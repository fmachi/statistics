package com.fmachi.n26.statistics.controllers;

import com.fmachi.n26.statistics.domain.Statistics;
import com.fmachi.n26.statistics.domain.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class StatisticsController {

    private final TransactionRepository transactionRepository;

    public StatisticsController(@Qualifier("TransactionRepository") TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping(path = "statistics", produces = "application/json")
    public Statistics getStatistics() {
        return transactionRepository.getStatistics();
    }
}
