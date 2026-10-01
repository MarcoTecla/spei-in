package com.mx.spei.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record SpeiInboundRequest(
        @NotBlank(message = "claveRastreo es obligatoria")
        String claveRastreo,
        @DecimalMin(value = "0.01", message = "monto debe ser mayor que 0")
        @Digits(integer = 16, fraction = 2, message = "monto debe tener máximo 2 decimales")
        BigDecimal monto,
        @NotBlank(message = "cuentaBeneficiaria es obligatoria")
        @Pattern(regexp = "\\d{18}", message = "cuentaBeneficiaria debe contener 18 dígitos")
        String cuentaBeneficiaria,
        String concepto) { }
