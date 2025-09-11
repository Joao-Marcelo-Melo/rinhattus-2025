package com.jmz.rinha.service;

import com.jmz.rinha.db.Database;
import com.jmz.rinha.model.Divida;
import com.jmz.rinha.model.ResultadoConsulta;

import java.time.Instant;
import java.util.UUID;

public class DividaService {

    private final Database database = Database.getInstance();

    public boolean registrarDivida(UUID identificador, double valor) {
        Divida divida = new Divida(identificador, valor);
        return database.salvar(divida);
    }

    public ResultadoConsulta consultar(Instant from, Instant to) {
        return database.consultar(from, to);
    }
}
