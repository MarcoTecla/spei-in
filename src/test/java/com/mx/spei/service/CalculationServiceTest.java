package com.mx.spei.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mx.spei.config.SpeiProperties;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CalculationServiceTest {
    private final CalculationService service = new CalculationService(
            new SpeiProperties(new BigDecimal("0.005"), new BigDecimal("8.00")));

    @Test
    void calculatesExampleAmounts() {
        assertEquals(new BigDecimal("1000.00"), service.principal(new BigDecimal("1000.00")));
        assertEquals(new BigDecimal("5.00"), service.spread(new BigDecimal("1000.00")));
        assertEquals(new BigDecimal("8.00"), service.commission());
    }

    @Test
    void roundsSpreadHalfUpToTwoDecimals() {
        assertEquals(new BigDecimal("0.01"), service.spread(new BigDecimal("1.00")));
    }
}
