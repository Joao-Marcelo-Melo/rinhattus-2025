package com.jmz.rinha.controller;

import com.jmz.rinha.database.Database;
import com.jmz.rinha.database.Divida;
import com.jmz.rinha.database.dtos.ConsultaDividasResponse;
import com.jmz.rinha.database.dtos.NovaDividaRequest;
import com.jmz.rinha.database.dtos.RespostaSimples;
import org.apache.coyote.Response;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/dividas")
class DividaController {

    @PostMapping
    public RespostaSimples criar(@RequestBody NovaDividaRequest req) {
        try {
            Divida d = new Divida(req.identificador(), req.valor(), Instant.now());
            Database.salvar(d);
            return new RespostaSimples(200, "Dívida registrada com sucesso");
        } catch (Exception e) {
            return new RespostaSimples(500, "Não foi possível realizar o recebimento da dívida");
        }
    }

    @GetMapping
    public ConsultaDividasResponse consultar(@RequestParam Instant from,
                                             @RequestParam Instant to) {
        try {
            List<Divida> lista = Database.buscar(from, to);
            long quantidade = lista.size();
            BigDecimal valorTotal = lista.stream()
                    .map(Divida::valor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return new ConsultaDividasResponse(quantidade, valorTotal);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar");
        }
    }
}
