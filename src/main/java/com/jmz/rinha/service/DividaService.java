package com.jmz.rinha.service;

import com.jmz.rinha.db.OffHeapDatabase;
import com.jmz.rinha.model.ResultadoConsulta;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DividaService {

    private final OffHeapDatabase database = new OffHeapDatabase(86_400);

    public void registrarDivida(double valor) {
        long epochSec = System.currentTimeMillis() / 1000;
        database.salvar(valor, epochSec);
    }

    public ResultadoConsulta consultar(Instant from, Instant to) {
        return database.consultar(from, to);
    }

    public void limparBase() {
        database.limpar();
    }
}
