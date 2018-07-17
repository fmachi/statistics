package com.fmachi.n26.statistics.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fmachi.n26.statistics.domain.Transaction;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.ZERO;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StatisticsDTO {
    @Builder.Default
    BigDecimal sum = ZERO;
    @Builder.Default
    BigDecimal avg = ZERO;
    @Builder.Default
    BigDecimal max = ZERO;
    @Builder.Default
    BigDecimal min = ZERO;
    @Builder.Default
    Integer count = 0;
}
