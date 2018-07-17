package com.fmachi.n26.statistics.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    @Test
    void nowShouldBeATransactionNotOlderThan60Seconds() {

        Transaction transaction = new Transaction(System.currentTimeMillis(), null);

        assertTrue(transaction.notOlderThan60Seconds());
    }

    @Test
    void nowMin35SecondsShouldBeATransactionNotOlderThan60Seconds() {

        Transaction transaction = new Transaction(System.currentTimeMillis() - 35000, null);

        assertTrue(transaction.notOlderThan60Seconds());

    }

    @Test
    void nowMin60SecondsShouldBeATransactionNotOlderThan60Seconds() {

        Transaction transaction = new Transaction(System.currentTimeMillis() - 60000, null);

        assertTrue(transaction.notOlderThan60Seconds());

    }

    @Test
    void nowMin60001MillisecondsShouldBeATransactionNotOlderThan60Seconds() {

        Transaction transaction = new Transaction(System.currentTimeMillis() - 60001, null);

        assertFalse(transaction.notOlderThan60Seconds());

    }


}