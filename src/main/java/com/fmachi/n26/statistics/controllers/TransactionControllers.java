package com.fmachi.n26.statistics.controllers;

import com.fmachi.n26.statistics.domain.Transaction;
import com.fmachi.n26.statistics.domain.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TransactionControllers {

    private final TransactionRepository transactionRepository;

    public TransactionControllers(@Qualifier("TransactionRepository") TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @PostMapping(path = "transactions", consumes = "application/json")
    public ResponseEntity<Void>  addTransaction(@RequestBody Transaction transaction) {
        log.info("Received transaction {}",transaction);
        if(transaction.notOlderThan60Seconds()) {
            transactionRepository.addTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.noContent().build();
    }
}
