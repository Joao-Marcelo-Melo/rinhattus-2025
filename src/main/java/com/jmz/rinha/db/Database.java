package com.jmz.rinha.db;

import com.jmz.rinha.model.Divida;
import com.jmz.rinha.model.ResultadoConsulta;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

public class Database {

    private final ConcurrentHashMap<UUID, Divida> dividas = new ConcurrentHashMap<>();

    private final LongAdder quantidadeTotal = new LongAdder();
    private final DoubleAdder valorTotal = new DoubleAdder();

    private static final Database INSTANCE = new Database();

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }

    public boolean salvar(Divida divida) {
        Divida anterior = dividas.putIfAbsent(divida.getIdentificador(), divida);
        if (anterior == null) {
            quantidadeTotal.increment();
            valorTotal.add(divida.getValor());
            return true;
        }
        return false;
    }

    public ResultadoConsulta consultar(Instant from, Instant to) {
        LongAdder count = new LongAdder();
        DoubleAdder total = new DoubleAdder();

        dividas.values().forEach(d -> {
            if (!d.getCriadoEm().isBefore(from) && d.getCriadoEm().isBefore(to)) {
                count.increment();
                total.add(d.getValor());
            }
        });

        return new ResultadoConsulta(count.sum(), total.sum());
    }

    public ResultadoConsulta resumoGeral() {
        return new ResultadoConsulta(
                quantidadeTotal.sum(),
                valorTotal.sum()
        );
    }
}
