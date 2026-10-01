package com.mx.spei.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mx.spei.dto.SpeiInboundRequest;
import com.mx.spei.dto.SpeiResponse;
import com.mx.spei.service.SpeiService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/spei")
public class SpeiController {
    private final SpeiService service;

    public SpeiController(SpeiService service) { this.service = service; }

    @PostMapping("/inbound")
    public ResponseEntity<SpeiResponse> inbound(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody SpeiInboundRequest request) {
        return ResponseEntity.ok(service.process(request, idempotencyKey));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpeiResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }
    
    @GetMapping("/{savingsId}/journalentries")
    public ResponseEntity<JsonNode> journalEntries(@PathVariable String savingsId) {
        return ResponseEntity.ok(service.getJournalEntries(savingsId));
    }
}
