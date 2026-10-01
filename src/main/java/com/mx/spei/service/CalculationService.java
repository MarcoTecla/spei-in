package com.mx.spei.service;

import com.mx.spei.config.SpeiProperties;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class CalculationService {
    private static final int SCALE = 2;
    private final SpeiProperties properties;

    public CalculationService(SpeiProperties properties) { this.properties = properties; }

    public BigDecimal principal(BigDecimal amount) { return amount.setScale(SCALE, RoundingMode.HALF_UP); }

    public BigDecimal spread(BigDecimal amount) {
        return amount.multiply(properties.spread()).setScale(SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal commission() {
        return properties.commission().setScale(SCALE, RoundingMode.HALF_UP);
    }
}
