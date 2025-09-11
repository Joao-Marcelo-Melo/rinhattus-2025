package com.jmz.rinha.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Divida {
    private final UUID identificador;
    private final double valor;
    private final Instant criadoEm;

    public Divida(UUID identificador, double valor) {
        this.identificador = identificador;
        this.valor = valor;
        this.criadoEm = Instant.now();
    }

    public UUID getIdentificador() {
        return identificador;
    }

    public double getValor() {
        return valor;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
