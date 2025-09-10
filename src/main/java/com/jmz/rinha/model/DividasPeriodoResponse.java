package com.jmz.rinha.model;

import java.math.BigDecimal;

public record DividasPeriodoResponse(
        int quantidadeTotal,
        BigDecimal valorTotal
) {}
