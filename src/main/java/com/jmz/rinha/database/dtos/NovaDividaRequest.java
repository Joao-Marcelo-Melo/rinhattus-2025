package com.jmz.rinha.database.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record NovaDividaRequest(UUID identificador, BigDecimal valor) {}