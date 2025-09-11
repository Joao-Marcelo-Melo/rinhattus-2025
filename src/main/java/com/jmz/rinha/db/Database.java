package com.jmz.rinha.db;

import com.jmz.rinha.model.Divida;
import com.jmz.rinha.model.ResultadoConsulta;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

@Component
public class Database {

    private final ConcurrentHashMap<UUID, Divida> dividas = new ConcurrentHashMap<>(1 << 20);

    private final LongAdder quantidadeTotal = new LongAdder();
    private final DoubleAdder valorTotal = new DoubleAdder();

    private static final Database INSTANCE = new Database();

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }

    public boolean salvar(Divida divida) {
        double valor = divida.getValor();
        UUID id = divida.getIdentificador();

        Divida anterior = dividas.putIfAbsent(id, divida);
        if (anterior == null) {
            quantidadeTotal.increment();
            valorTotal.add(valor);
            return true;
        }
        return false;
    }

    public ResultadoConsulta consultar(Instant from, Instant to) {
        long count = 0L;
        double total = 0.0;

        for (Divida d : dividas.values()) {
            Instant criadoEm = d.getCriadoEm();
            if (!criadoEm.isBefore(from) && criadoEm.isBefore(to)) {
                count++;
                total += d.getValor();
            }
        }
        return new ResultadoConsulta(count, total);
    }


    public ResultadoConsulta resumoGeral() {
        return new ResultadoConsulta(
                quantidadeTotal.sum(),
                valorTotal.sum()
        );
    }

    public void limparDatabase() {
        dividas.clear();
        quantidadeTotal.reset();
        valorTotal.reset();
    }
}
