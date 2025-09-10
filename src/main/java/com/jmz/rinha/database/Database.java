package com.jmz.rinha.database;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private static final Map<UUID, Divida> DIVIDAS = new ConcurrentHashMap<>();

    public static void salvar(Divida divida) {
        if (DIVIDAS.putIfAbsent(divida.identificador(), divida) != null) {
            throw new RuntimeException("Duplicado");
        }
    }

    public static List<Divida> buscar(Instant from, Instant to) {
        return DIVIDAS.values().stream()
                .filter(d -> !d.criadaEm().isBefore(from) && d.criadaEm().isBefore(to))
                .toList();
    }
}
