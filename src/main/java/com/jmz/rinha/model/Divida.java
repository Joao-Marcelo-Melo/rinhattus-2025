package com.jmz.rinha.model;

import java.math.BigDecimal;

public record Divida(
        String identificador,
        BigDecimal valor,
        long timestamp
) {
    public Divida(String identificador, BigDecimal valor) {
        this(identificador, valor, System.currentTimeMillis());
    }

    public boolean isValid() {
        return identificador != null &&
                identificador.length() == 36 &&
                valor != null &&
                valor.compareTo(BigDecimal.ZERO) > 0;
    }
}
