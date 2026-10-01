package com.mx.spei.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SpeiResponse(
        UUID id,
        String claveRastreo,
        String cuentaBeneficiaria,
        Long fineractClientId,
        String fineractClientName,
        Long fineractSavingsId,
        BigDecimal principal,
        BigDecimal spread,
        BigDecimal commission,
        Long fineractDepositTxId,
        Long fineractSpreadTxId,
        Long fineractCommissionTxId,
        OffsetDateTime processedAt,
        String status) { }
