package com.jmz.rinha.service;

import com.jmz.rinha.model.Divida;
import com.jmz.rinha.repository.DividaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class DividaService {

    private final DividaRepository repo;

    public DividaService(DividaRepository repo) {
        this.repo = repo;
    }

    @Transactional //TODO ENTENDER OQ É ESSE CARA
    public void registrar(UUID id, BigDecimal valor) {
        if (repo.existsById(id)) return;
        Divida divida = new Divida();

        divida.setId(id);
        divida.setValor(valor);
        repo.save(divida);
    }

    @Transactional(readOnly = true) //TODO ENTENDER OQ É ESSE CARA
    public Resumo resumo(Instant from, Instant to) {
        Object[] result = repo.getResumo(from, to);
        return new Resumo(((Number) result[0]).intValue(), (BigDecimal) result[1]);
    }

    public record Resumo(int quantidadeTotal, BigDecimal valorTotal) {}
}
