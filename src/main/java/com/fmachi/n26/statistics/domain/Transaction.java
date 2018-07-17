package com.fmachi.n26.statistics.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {

    Long timestamp;
    BigDecimal amount;

    public boolean notOlderThan60Seconds() {
        return System.currentTimeMillis()-60000<=timestamp;
    }
}
