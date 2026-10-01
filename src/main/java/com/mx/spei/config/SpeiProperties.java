package com.mx.spei.config;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spei")
public record SpeiProperties(BigDecimal spread, BigDecimal commission) { }
