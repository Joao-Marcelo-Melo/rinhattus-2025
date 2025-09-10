package com.jmz.rinha.database;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Divida(UUID identificador, BigDecimal valor, Instant criadaEm) {}