package com.jmz.rinha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmz.rinha.model.CreateDividaRequest;
import com.jmz.rinha.model.Divida;
import com.jmz.rinha.model.DividasPeriodoResponse;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DividaService {

    private static final String TIMELINE_KEY = "timeline";
    private static final String HASH_KEY = "dividas";

    private final ReactiveRedisTemplate<String, String> redis;
    private final ObjectMapper mapper;
    private final RedisScript<Long> saveScript;
    private final RedisScript<List> aggregateScript;

    private static final String SAVE_LUA = """
        local hashKey = KEYS[1]
        local timelineKey = KEYS[2]
        local id = ARGV[1]
        local dividaJson = ARGV[2]
        local ts = ARGV[3]
        if redis.call('HEXISTS', hashKey, id) == 1 then
          return 0
        end
        redis.call('HSET', hashKey, id, dividaJson)
        redis.call('ZADD', timelineKey, ts, id)
        return 1
        """;

    private static final String AGGREGATE_LUA = """
        local timelineKey = KEYS[1]
        local hashKey = KEYS[2]
        local fromTs = ARGV[1]
        local toTs = ARGV[2]
        local ids = redis.call('ZRANGEBYSCORE', timelineKey, fromTs, '(' .. toTs)
        if #ids == 0 then return {0,'0.00'} end
        local valores = redis.call('HMGET', hashKey, unpack(ids))
        local count, total = 0, 0.0
        for i,v in ipairs(valores) do
          if v then
            local n = string.match(v, '"valor":([0-9%.]+)')
            total = total + tonumber(n)
            count = count + 1
          end
        end
        return {count, string.format("%.2f", total)}
        """;

    public DividaService(ReactiveRedisTemplate<String, String> redis, ObjectMapper mapper) {
        this.redis = redis;
        this.mapper = mapper;
        this.saveScript = RedisScript.of(SAVE_LUA, Long.class);
        this.aggregateScript = RedisScript.of(AGGREGATE_LUA, List.class);
    }

    public Mono<Void> save(CreateDividaRequest request) {
        Divida divida = new Divida(request.identificador(), request.valor());
        if (!divida.isValid()) return Mono.error(new IllegalArgumentException());

        return Mono.fromCallable(() -> mapper.writeValueAsString(divida))
                .flatMap(json -> redis.execute(
                                        saveScript,
                                        List.of(HASH_KEY, TIMELINE_KEY),
                                        List.of(divida.identificador(), json, String.valueOf(divida.timestamp()))
                                )
                                .next()
                )
                .filter(result -> result != null && result > 0)
                .switchIfEmpty(Mono.error(new IllegalStateException("Dívida já existe")))
                .then();
    }

    public Mono<DividasPeriodoResponse> findByPeriod(LocalDateTime from, LocalDateTime to) {
        long fromTs = from.toInstant(ZoneOffset.UTC).toEpochMilli();
        long toTs = to.toInstant(ZoneOffset.UTC).toEpochMilli();

        return redis.execute(
                        aggregateScript,
                        List.of(TIMELINE_KEY, HASH_KEY),
                        String.valueOf(fromTs),
                        String.valueOf(toTs)
                )
                .cast(List.class)
                .next()
                .map(res -> {
                    if (res == null || res.isEmpty()) {
                        return new DividasPeriodoResponse(0, BigDecimal.ZERO);
                    }
                    Integer count = Integer.valueOf(res.get(0).toString());
                    BigDecimal total = new BigDecimal(res.get(1).toString());
                    return new DividasPeriodoResponse(count, total);
                });
    }

}
