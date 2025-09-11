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

    private final ConcurrentHashMap<Long, Bucket> buckets = new ConcurrentHashMap<>();

    private final LongAdder quantidadeTotal = new LongAdder();
    private final DoubleAdder valorTotal = new DoubleAdder();

    private static final Database INSTANCE = new Database();

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }

    public boolean salvar(Divida divida) {
        double valor = divida.getValor();
        Instant criadoEm = divida.getCriadoEm();

        long bucketKey = toBucketKey(criadoEm);
        buckets.computeIfAbsent(bucketKey, k -> new Bucket()).add(valor);

        quantidadeTotal.increment();
        valorTotal.add(valor);
        return true;
    }

    public ResultadoConsulta consultar(Instant from, Instant to) {
        long startKey = toBucketKey(from);
        long endKey = toBucketKey(to);

        long count = 0;
        double total = 0.0;

        for (long key = startKey; key <= endKey; key++) {
            Bucket bucket = buckets.get(key);
            if (bucket != null) {
                count += bucket.quantidade.sum();
                total += bucket.valor.sum();
            }
        }
        return new ResultadoConsulta(count, total);
    }

    public ResultadoConsulta resumoGeral() {
        return new ResultadoConsulta(quantidadeTotal.sum(), valorTotal.sum());
    }

    public void limparDatabase() {
        buckets.clear();
        quantidadeTotal.reset();
        valorTotal.reset();
    }

    private long toBucketKey(Instant instant) {
        return instant.getEpochSecond();
    }
}
