package com.jmz.rinha.controller;

import com.jmz.rinha.model.ConsultaResponse;
import com.jmz.rinha.model.DividaRequest;
import com.jmz.rinha.model.DividaResponse;
import com.jmz.rinha.model.ResultadoConsulta;
import com.jmz.rinha.service.DividaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/dividas")
public class DividaController {

    private final DividaService service;
    private static final DividaResponse SUCCESS_RESPONSE = new DividaResponse(200, "Dívida registrada com sucesso");
    private static final DividaResponse ERROR_RESPONSE = new DividaResponse(500, "Não foi possível realizar o recebimento da dívida");
    private static final ResponseEntity<DividaResponse> SUCCESS_ENTITY = ResponseEntity.ok(SUCCESS_RESPONSE);
    private static final ResponseEntity<DividaResponse> ERROR_ENTITY = ResponseEntity.status(500).body(ERROR_RESPONSE);
    private static final ConsultaResponse ZERO_RESPONSE = new ConsultaResponse(0, 0.0);
    private static final ResponseEntity<ConsultaResponse> ZERO_RESPONSE_ENTITY = ResponseEntity.ok(ZERO_RESPONSE);

    public DividaController(DividaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DividaResponse> registrar(@RequestBody DividaRequest body) {
        try {
            UUID identificador = body.identificador();
            double valor = body.valor();
            if (identificador == null || valor <= 0) {
                return ERROR_ENTITY;
            }
            boolean success = service.registrarDivida(identificador, valor);
            return success ? SUCCESS_ENTITY : ERROR_ENTITY;
        } catch (Exception e) {
            return ERROR_ENTITY;
        }
    }

    @GetMapping
    public ResponseEntity<ConsultaResponse> consultar(
            @RequestParam(value = "from", required = false) Instant from,
            @RequestParam(value = "to", required = false) Instant to
    ) {
        if (from == null || to == null) return ZERO_RESPONSE_ENTITY;
        ResultadoConsulta resultado = service.consultar(from, to);
        return ResponseEntity.ok(new ConsultaResponse(resultado.quantidadeTotal(), resultado.valorTotal()));
    }
}
