package com.jmz.rinha.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateDividaRequest(
        @NotBlank String identificador,
        @NotNull @DecimalMin("0.01") BigDecimal valor
) {}
