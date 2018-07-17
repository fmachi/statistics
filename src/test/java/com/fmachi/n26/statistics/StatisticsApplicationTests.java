package com.fmachi.n26.statistics;

import com.fmachi.n26.statistics.domain.Transaction;
import com.fmachi.n26.statistics.dtos.StatisticsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsApplicationTests {

    private static final String LOCALHOST_URL = "http://localhost";

    @LocalServerPort
    private int localServerPort;


    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRejectExpiredTransaction() {
        ResponseEntity<Void> response = restTemplate.postForEntity(
                getTransactionUrl(),
                new Transaction(now() - seconds(120), new BigDecimal("44.25")),
                Void.class);

        checkStatus(HttpStatus.NO_CONTENT, response.getStatusCode());

    }

    @Test
    void shouldCalculateStatistics() {
        IntStream.rangeClosed(1,25).forEach(
                i -> {
                    ResponseEntity<Void> response = restTemplate.postForEntity(
                            getTransactionUrl(),
                            new Transaction(now() - seconds(30-i), new BigDecimal(i+"4.25")),
                            Void.class);

                    checkStatus(HttpStatus.CREATED, response.getStatusCode());
                }
        );

        ResponseEntity<StatisticsDTO> response = restTemplate.getForEntity(getStatisticsUrl(), StatisticsDTO.class);

        checkStatus(HttpStatus.OK, response.getStatusCode());

        StatisticsDTO statistics = response.getBody();
        Assertions.assertEquals(new BigDecimal("134.25"), statistics.getAvg());
        Assertions.assertEquals(new BigDecimal("14.25"), statistics.getMin());
        Assertions.assertEquals(new BigDecimal("254.25"), statistics.getMax());
        Assertions.assertEquals(new BigDecimal("3356.25"), statistics.getSum());
        Assertions.assertEquals(Integer.valueOf(25), statistics.getCount());
    }

    @Test
    void shouldRetrieveEmptyStatistics() {
        ResponseEntity<StatisticsDTO> response = restTemplate.getForEntity(getStatisticsUrl(), StatisticsDTO.class);

        checkStatus(HttpStatus.OK, response.getStatusCode());

        StatisticsDTO statistics = response.getBody();
        Assertions.assertEquals(ZERO, statistics.getAvg());
        Assertions.assertEquals(ZERO, statistics.getMin());
        Assertions.assertEquals(ZERO, statistics.getMax());
        Assertions.assertEquals(ZERO, statistics.getSum());
        Assertions.assertEquals(Integer.valueOf(0), statistics.getCount());

    }

    private void checkStatus(HttpStatus expected, HttpStatus statusCode) {
        assertEquals(expected, statusCode);
    }

    private Long seconds(int seconds) {
        return 1000l * seconds;
    }

    private Long now() {
        return System.currentTimeMillis();
    }


    private String getStatisticsUrl() {
        return getBaseUrl() + "/statistics";
    }

    private String getTransactionUrl() {
        return getBaseUrl() + "/transactions";
    }

    private String getBaseUrl() {
        return LOCALHOST_URL + ":" + localServerPort;
    }

}
