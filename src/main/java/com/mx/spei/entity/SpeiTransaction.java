package com.mx.spei.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "spei_transactions")
@Getter @Setter @NoArgsConstructor
public class SpeiTransaction {
    public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getIdempotencyKey() {
		return idempotencyKey;
	}
	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}
	public String getClaveRastreo() {
		return claveRastreo;
	}
	public void setClaveRastreo(String claveRastreo) {
		this.claveRastreo = claveRastreo;
	}
	public String getCuentaBeneficiaria() {
		return cuentaBeneficiaria;
	}
	public void setCuentaBeneficiaria(String cuentaBeneficiaria) {
		this.cuentaBeneficiaria = cuentaBeneficiaria;
	}
	public Long getFineractClientId() {
		return fineractClientId;
	}
	public void setFineractClientId(Long fineractClientId) {
		this.fineractClientId = fineractClientId;
	}
	public String getFineractClientName() {
		return fineractClientName;
	}
	public void setFineractClientName(String fineractClientName) {
		this.fineractClientName = fineractClientName;
	}
	public Long getFineractSavingsId() {
		return fineractSavingsId;
	}
	public void setFineractSavingsId(Long fineractSavingsId) {
		this.fineractSavingsId = fineractSavingsId;
	}
	public BigDecimal getPrincipal() {
		return principal;
	}
	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}
	public BigDecimal getSpread() {
		return spread;
	}
	public void setSpread(BigDecimal spread) {
		this.spread = spread;
	}
	public BigDecimal getCommission() {
		return commission;
	}
	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}
	public Long getFineractDepositTxId() {
		return fineractDepositTxId;
	}
	public void setFineractDepositTxId(Long fineractDepositTxId) {
		this.fineractDepositTxId = fineractDepositTxId;
	}
	public Long getFineractSpreadTxId() {
		return fineractSpreadTxId;
	}
	public void setFineractSpreadTxId(Long fineractSpreadTxId) {
		this.fineractSpreadTxId = fineractSpreadTxId;
	}
	public Long getFineractCommissionTxId() {
		return fineractCommissionTxId;
	}
	public void setFineractCommissionTxId(Long fineractCommissionTxId) {
		this.fineractCommissionTxId = fineractCommissionTxId;
	}
	public OffsetDateTime getProcessedAt() {
		return processedAt;
	}
	public void setProcessedAt(OffsetDateTime processedAt) {
		this.processedAt = processedAt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Id
    private UUID id;
    @jakarta.persistence.Column(nullable = false, unique = true)
    private String idempotencyKey;
    private String claveRastreo;
    private String cuentaBeneficiaria;
    private Long fineractClientId;
    private String fineractClientName;
    private Long fineractSavingsId;
    private BigDecimal principal;
    private BigDecimal spread;
    private BigDecimal commission;
    private Long fineractDepositTxId;
    private Long fineractSpreadTxId;
    private Long fineractCommissionTxId;
    private OffsetDateTime processedAt;
    private String status;
}
