package com.jmz.rinha.config;

import com.jmz.rinha.service.DividaService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.UUID;

@Configuration
public class WarmUp {

    private static final int WARMUP_ENTRIES = 50000;
    private static final int WARMUP_ITERATIONS = 1000;

    @Bean
    ApplicationRunner aggressiveWarmup(DividaService service) {
        return args -> {
            System.out.println("🔥 Iniciando warmup AGRESSIVO...");
            long start = System.currentTimeMillis();
            warmupParsing();
            warmupService(service);
            warmupConsultas(service);
            warmupJIT(service);
            System.gc();
            Thread.sleep(100);
            System.gc();
            long duration = System.currentTimeMillis() - start;
            System.out.println("✅ Warmup concluído em " + duration + "ms");
        };
    }

    private void warmupParsing() {
        for (int i = 0; i < 10000; i++) {
            Instant.parse("2020-07-10T12:34:56.000Z");
            Instant.parse("2020-07-10T12:35:56.123Z");
        }
        for (int i = 0; i < 10000; i++) {
            UUID.randomUUID();
            UUID.fromString("4a7901b8-7d26-4d9d-aa19-4dc1c7cf60b3");
        }
    }

    private void warmupService(DividaService service) {
        for (int i = 0; i < WARMUP_ENTRIES; i++) {
            service.registrarDivida(
                    UUID.randomUUID(),
                    50.0 + (i % 1000)
            );
            if (i % 1000 == 0) {
                service.consultar(
                        Instant.parse("2019-01-01T00:00:00Z"),
                        Instant.parse("2035-01-01T00:00:00Z")
                );
            }
        }
    }

    private void warmupConsultas(DividaService service) {
        Instant base = Instant.parse("2020-01-01T00:00:00Z");

        for (int i = 0; i < 500; i++) {
            Instant from = base.plusSeconds(i * 3600);
            Instant to = from.plusSeconds(86400);
            service.consultar(from, to);
            Instant from2 = base.plusSeconds(i * 60);
            Instant to2 = from2.plusSeconds(3600);
            service.consultar(from2, to2);
            Instant from3 = base.plusSeconds(i);
            Instant to3 = from3.plusSeconds(60);
            service.consultar(from3, to3);
        }
    }

    private void warmupJIT(DividaService service) {
        for (int iteration = 0; iteration < 10; iteration++) {
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                UUID id = UUID.randomUUID();
                service.registrarDivida(id, 115.45);

                if (i % 100 == 0) {
                    service.consultar(
                            Instant.parse("2020-07-10T12:34:56.000Z"),
                            Instant.parse("2020-07-10T12:35:56.000Z")
                    );
                }
            }
        }
    }
}