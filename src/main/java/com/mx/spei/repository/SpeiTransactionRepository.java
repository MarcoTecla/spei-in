package com.mx.spei.repository;

import com.mx.spei.entity.SpeiTransaction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeiTransactionRepository extends JpaRepository<SpeiTransaction, UUID> {
    Optional<SpeiTransaction> findByIdempotencyKey(String idempotencyKey);
}
