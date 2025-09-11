package com.jmz.rinha.service;

import com.jmz.rinha.db.OffHeapDatabase;
import com.jmz.rinha.model.ResultadoConsulta;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class DividaService {

    private final OffHeapDatabase database = new OffHeapDatabase(86_400); // 1 dia de segundos

    public boolean registrarDivida(UUID identificador, double valor) {
        database.salvar(valor, Instant.now());
        return true; // não falha no modelo atual
    }

    public ResultadoConsulta consultar(Instant from, Instant to) {
        return database.consultar(from, to);
    }

    public void limparBase() {
        database.limpar();
    }
}
