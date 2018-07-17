package com.fmachi.n26.statistics.persistence.inmemory;

import com.fmachi.n26.statistics.domain.Statistics;
import com.fmachi.n26.statistics.domain.Transaction;
import com.fmachi.n26.statistics.domain.TransactionRepository;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ThreadSafeTransactionRepository implements TransactionRepository {

    private final ExecutorService executorService;
    private final BlockingQueue<Transaction> eventBus;
    private final AtomicReference<Statistics> statisticsHolder = new AtomicReference<>(Statistics.builder().build());
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicInteger transactionCount = new AtomicInteger(0);
    private final TransactionRepository innerRepository;

    public ThreadSafeTransactionRepository(TransactionRepository innerRepository, int capacity) {
        this.innerRepository = innerRepository;
        executorService = Executors.newSingleThreadExecutor();
        eventBus = new LinkedBlockingQueue<>(capacity);
        log.info("Queue capacity is {}",capacity);
    }

    @PostConstruct
    public void init() {
        log.info("Init async processing");
        executorService.submit(
                new StatisticsUpdater()
        );
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroy async processing");
        running.set(false);
        executorService.shutdownNow();
    }

    @Override
    public void addTransaction(Transaction transaction) {
        eventBus.offer(
                transaction
        );
    }

    @Override
    public Statistics getStatistics() {
        return statisticsHolder.get();
    }

    @Override
    public void purge() {
        log.warn("Purge will be performed automatically when a transaction expires");
    }

    @Override
    public int getTransactionCount() {
        return transactionCount.get();
    }

    class StatisticsUpdater implements Runnable {

        @Override
        public void run() {
            long timeout = 0l;
            log.info("Statistics updater starting");
            while (running.get()) {
                Transaction transaction = getEventWithTimeout(timeout);
                if (transaction != null) {
                    log.info("Adding a new transaction");
                    innerRepository.addTransaction(transaction);
                } else {
                    log.info("Purging expired transaction");
                }
                innerRepository.purge();

                transactionCount.set(innerRepository.getTransactionCount());

                Statistics statistics = innerRepository.getStatistics();
                log.info("Updating statistics {}", statistics);
                statisticsHolder.set(
                        statistics
                );

                timeout = calculateTimeout(statistics.getMinTimestamp());
                log.info("Timeout is {}", timeout);
            }
            log.info("Statistics updater ends");
        }

        private long calculateTimeout(Long minTimestamp) {
            long nextExpirationTime = minTimestamp + 60000 + 1;
            long now = System.currentTimeMillis();
            return Math.max(nextExpirationTime - now, 0);
        }

        private Transaction getEventWithTimeout(long timeoutMillis) {
            try {
                if (timeoutMillis > 0) {
                    return eventBus.poll(timeoutMillis, TimeUnit.MILLISECONDS);
                } else {
                    return eventBus.take();
                }
            } catch (InterruptedException e) {
                log.error("Statistics updater thread interrupted");
                throw new RuntimeException("Statistics updater thread interrupted", e);
            }
        }
    }
}
