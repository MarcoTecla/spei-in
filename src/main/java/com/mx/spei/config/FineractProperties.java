package com.mx.spei.config;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fineract")
public record FineractProperties(
        String baseUrl,
        String username,
        String password,
        String tenantId,
        Long paymentTypeId) { }
