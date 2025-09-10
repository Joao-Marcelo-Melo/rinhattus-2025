package com.jmz.rinha.service;

import io.lettuce.core.KeyValue;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DividaService {

    private final RedisAsyncCommands<String, String> redis;

    public void registrarDivida(UUID identificador, BigDecimal valor) {
        long cents = valor.movePointRight(2).longValueExact();
        long epochSecond = Instant.now().getEpochSecond();

        // SET NX + incrementos no mesmo pipeline
        redis.set("debt:" + identificador, "1", SetArgs.Builder.nx());
        redis.incrby("sum:" + epochSecond, cents);
        redis.incrby("count:" + epochSecond, 1);
        redis.flushCommands(); // envia em batch
    }

    public Map<String, Object> consultar(Instant from, Instant to) throws Exception {
        String[] sumKeys = buildKeys("sum:", from, to);
        String[] countKeys = buildKeys("count:", from, to);

        // Rodando em paralelo
        var sumsFuture = redis.mget(sumKeys).toCompletableFuture();
        var countsFuture = redis.mget(countKeys).toCompletableFuture();

        CompletableFuture.allOf(sumsFuture, countsFuture).join();

        List<KeyValue<String, String>> sums = sumsFuture.get();
        List<KeyValue<String, String>> counts = countsFuture.get();

        long totalCents = 0L;
        long totalCount = 0L;

        for (KeyValue<String, String> kv : sums) {
            if (kv.hasValue()) totalCents += Long.parseLong(kv.getValue());
        }
        for (KeyValue<String, String> kv : counts) {
            if (kv.hasValue()) totalCount += Long.parseLong(kv.getValue());
        }

        // Usando HashMap (menos overhead que Map.of)
        Map<String, Object> result = new HashMap<>(2);
        result.put("quantidadeTotal", totalCount);
        result.put("valorTotal", BigDecimal.valueOf(totalCents, 2));
        return result;
    }

    private String[] buildKeys(String prefix, Instant from, Instant to) {
        long start = from.getEpochSecond();
        long end = to.getEpochSecond();
        int len = (int) (end - start);
        String[] keys = new String[len];
        for (int i = 0; i < len; i++) {
            keys[i] = prefix + (start + i);
        }
        return keys;
    }
}
