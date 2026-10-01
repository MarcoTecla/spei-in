package com.mx.spei.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mx.spei.client.FineractClient;
import com.mx.spei.dto.SpeiInboundRequest;
import com.mx.spei.dto.SpeiResponse;
import com.mx.spei.entity.SpeiTransaction;
import com.mx.spei.exception.AccountValidationException;
import com.mx.spei.repository.SpeiTransactionRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SpeiService {
    private final SpeiTransactionRepository repository;
    private final FineractClient fineractClient;
    private final CalculationService calculationService;

    public SpeiService(SpeiTransactionRepository repository, FineractClient fineractClient,
                       CalculationService calculationService) {
        this.repository = repository;
        this.fineractClient = fineractClient;
        this.calculationService = calculationService;
    }

    public SpeiResponse process(SpeiInboundRequest request, String idempotencyKey) {
        String key = idempotencyKey == null || idempotencyKey.isBlank()
                ? UUID.randomUUID().toString() : idempotencyKey;

        var existing = repository.findByIdempotencyKey(key);
        if (existing.isPresent()) return toResponse(existing.get());

        JsonNode account;
        try {
            account = fineractClient.findSavingsAccount(request.cuentaBeneficiaria());
        } catch (FineractClient.FineractException ex) {
            if (ex.status() == 404) throw new AccountValidationException("La CLABE no pertenece a ningún cliente en Fineract");
            throw new FineractClient.FineractException(502, ex.getMessage());
        }
        if (account == null || account.isEmpty()) throw new FineractClient.FineractException(502, "Respuesta vacía de Fineract");
        if (!account.path("status").path("active").asBoolean(false))
            throw new AccountValidationException("La cuenta Fineract está inactiva");
        if (!"MXN".equalsIgnoreCase(account.path("currency").path("code").asText()))
            throw new AccountValidationException("La cuenta Fineract no está denominada en MXN");

        long savingsId = account.path("id").asLong();
        BigDecimal principal = calculationService.principal(request.monto());
        BigDecimal spread = calculationService.spread(request.monto());
        BigDecimal commission = calculationService.commission();

        long principalTx = fineractClient.deposit(savingsId, principal, "SPEI-IN principal " + request.claveRastreo());
        long spreadTx = fineractClient.deposit(savingsId, spread, "SPEI-IN spread " + request.claveRastreo());
        long commissionTx = fineractClient.deposit(savingsId, commission, "SPEI-IN commission " + request.claveRastreo());

        SpeiTransaction tx = new SpeiTransaction();
        tx.setId(UUID.randomUUID());
        tx.setIdempotencyKey(key);
        tx.setClaveRastreo(request.claveRastreo());
        tx.setCuentaBeneficiaria(request.cuentaBeneficiaria());
        tx.setFineractClientId(account.path("clientId").asLong());
        tx.setFineractClientName(account.path("clientName").asText());
        tx.setFineractSavingsId(savingsId);
        tx.setPrincipal(principal);
        tx.setSpread(spread);
        tx.setCommission(commission);
        tx.setFineractDepositTxId(principalTx);
        tx.setFineractSpreadTxId(spreadTx);
        tx.setFineractCommissionTxId(commissionTx);
        tx.setProcessedAt(OffsetDateTime.now(ZoneOffset.UTC));
        tx.setStatus("APPLIED");
        return toResponse(repository.save(tx));
    }

    public SpeiResponse get(UUID id) {
        return repository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new java.util.NoSuchElementException("SPEI no encontrado"));
    }

    public JsonNode getJournalEntries(String savingsId) {
        try {
            return fineractClient.getJournalEntries(savingsId);
        } catch (FineractClient.FineractException ex) {
            throw new FineractClient.FineractException(502, ex.getMessage());
        }
    }
    
    private SpeiResponse toResponse(SpeiTransaction tx) {
        return new SpeiResponse(tx.getId(), tx.getClaveRastreo(), tx.getCuentaBeneficiaria(),
                tx.getFineractClientId(), tx.getFineractClientName(), tx.getFineractSavingsId(),
                tx.getPrincipal(), tx.getSpread(), tx.getCommission(), tx.getFineractDepositTxId(),
                tx.getFineractSpreadTxId(), tx.getFineractCommissionTxId(), tx.getProcessedAt(), tx.getStatus());
    }
}
