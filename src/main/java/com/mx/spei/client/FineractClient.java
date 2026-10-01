package com.mx.spei.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.mx.spei.config.FineractProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class FineractClient {
    private final RestClient client;
    private final FineractProperties properties;

    public FineractClient(RestClient.Builder builder, FineractProperties properties) {
        this.properties = properties;
        this.client = builder.baseUrl(properties.baseUrl()).build();
    }

    public JsonNode findSavingsAccount(String clabe) {
        try {
            return client.get()
                    .uri("/savingsaccounts/external-id/{externalId}", clabe)
                    .headers(h -> h.setBasicAuth(properties.username(), properties.password()))
                    .header("Fineract-Platform-TenantId", properties.tenantId())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException e) {
            throw new FineractException(e.getStatusCode().value(), extractMessage(e));
        }
    }
    
    public JsonNode getJournalEntries(String savingsId) {
        try {
            return client.get()
            		.uri(uriBuilder -> uriBuilder
                            .path("/journalentries")
                            .queryParam("savingsId", savingsId)
                            .queryParam("transactionDetails", true)
                            .queryParam("limit", 50)
                            .build())
            		.headers(h -> h.setBasicAuth(properties.username(), properties.password()))
                    .header("Fineract-Platform-TenantId", properties.tenantId())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException e) {
            throw new FineractException(e.getStatusCode().value(), extractMessage(e));
        }
    }

    public long deposit(long savingsId, BigDecimal amount, String note) {
        try {
            JsonNode response = client.post()
                    .uri("/savingsaccounts/{savingsId}/transactions?command=deposit", savingsId)
                    .headers(h -> h.setBasicAuth(properties.username(), properties.password()))
                    .header("Fineract-Platform-TenantId", properties.tenantId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new DepositRequest("en", "yyyy-MM-dd", LocalDate.now().toString(), amount, properties.paymentTypeId(), note))
                    .retrieve()
                    .body(JsonNode.class);
            if (response == null || response.get("resourceId") == null) {
                throw new FineractException(502, "Fineract no devolvió resourceId");
            }
            return response.get("resourceId").asLong();
        } catch (RestClientResponseException e) {
            throw new FineractException(e.getStatusCode().value(), extractMessage(e));
        }
    }

    private String extractMessage(RestClientResponseException e) {
        try {
            JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(e.getResponseBodyAsString());
            if (node.has("errors") && node.get("errors").isArray() && !node.get("errors").isEmpty()) {
                return node.get("errors").get(0).path("defaultUserMessage").asText(e.getMessage());
            }
            return node.path("defaultUserMessage").asText(e.getMessage());
        } catch (Exception ignored) {
            return e.getResponseBodyAsString().isBlank() ? e.getMessage() : e.getResponseBodyAsString();
        }
    }

    private record DepositRequest(String locale, String dateFormat, String transactionDate, BigDecimal transactionAmount, Long paymentTypeId, String note) { }

    public static class FineractException extends RuntimeException {
        private final int status;
        public FineractException(int status, String message) { super(message); this.status = status; }
        public int status() { return status; }
    }
}
