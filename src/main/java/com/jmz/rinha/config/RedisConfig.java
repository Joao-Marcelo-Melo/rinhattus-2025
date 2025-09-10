package com.jmz.rinha.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class RedisConfig {

    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return DefaultClientResources.builder()
                .eventExecutorGroup((EventExecutorGroup) Executors.newFixedThreadPool(2)) // só 2 threads
                .ioThreadPoolSize(2)
                .computationThreadPoolSize(2)
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient(ClientResources clientResources) {
        RedisClient client = RedisClient.create(clientResources, "redis://redis:6379/0");
        client.setOptions(ClientOptions.builder()
                .autoReconnect(true)
                .requestQueueSize(10_000) // fila grande o suficiente para burst
                .build());
        return client;
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, String> connection(RedisClient redisClient) {
        return redisClient.connect();
    }

    @Bean
    public RedisAsyncCommands<String, String> redisAsync(StatefulRedisConnection<String, String> connection) {
        // Auto-flush habilitado para evitar syncs desnecessários
        connection.setAutoFlushCommands(false);
        return connection.async();
    }
}
