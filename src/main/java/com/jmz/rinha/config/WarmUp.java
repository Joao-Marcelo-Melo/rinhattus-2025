package com.jmz.rinha.config;

import com.jmz.rinha.service.DividaService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class WarmUp {

    private static final int WARMUP_INSERTS = 500;
    private static final int WARMUP_QUERIES = 200;

    @Bean
    ApplicationRunner warmUpRunner(DividaService service) {
        return args -> {
            for (int i = 0; i < WARMUP_INSERTS; i++) {
                service.registrarDivida( (i % 100) + 1.0);
            }
            Instant base = Instant.parse("2020-01-01T00:00:00Z");
            for (int i = 0; i < WARMUP_QUERIES; i++) {
                service.consultar(base, base.plusSeconds(60));
                service.consultar(base, base.plusSeconds(3600));
                service.consultar(base, base.plusSeconds(86400));
            }
            System.gc();
            service.limparBase();
        };
    }
}
