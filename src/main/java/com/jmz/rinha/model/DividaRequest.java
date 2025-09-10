package com.jmz.rinha.model;

import java.math.BigDecimal;
import java.util.UUID;

public record DividaRequest(UUID identificador, BigDecimal valor) {}