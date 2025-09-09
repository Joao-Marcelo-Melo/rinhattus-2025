package com.jmz.rinha.repository;

import com.jmz.rinha.model.Divida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

public interface DividaRepository extends JpaRepository<Divida, UUID> {

    @Query("SELECT COUNT(d), COALESCE(SUM(d.valor),0) " +
            "FROM Divida d WHERE d.createdAt >= :from AND d.createdAt < :to")
    Object[] getResumo(Instant from, Instant to);
}
