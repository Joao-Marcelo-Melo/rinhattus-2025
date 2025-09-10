package com.jmz.rinha.controller;

import com.jmz.rinha.model.DividaRequest;
import com.jmz.rinha.service.DividaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dividas")
@RequiredArgsConstructor
public class DividaController {

    private final DividaService service;

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@RequestBody DividaRequest req) {
        if (req.identificador() == null || req.valor() == null || req.valor().compareTo(BigDecimal.ZERO) <= 0) {
            return errorResponse();
        }

        try {
            service.registrarDivida(req.identificador(), req.valor());

            Map<String, Object> resp = new HashMap<>(2);
            resp.put("status", 200);
            resp.put("mensagem", "Dívida registrada com sucesso");
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            return errorResponse();
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> consultar(@RequestParam Instant from, @RequestParam Instant to) {
        try {
            return ResponseEntity.ok(service.consultar(from, to));
        } catch (Exception e) {
            return errorResponse();
        }
    }

    private ResponseEntity<Map<String, Object>> errorResponse() {
        Map<String, Object> error = new HashMap<>(2);
        error.put("status", 500);
        error.put("mensagem", "Não foi possível realizar o recebimento da dívida");
        return ResponseEntity.status(500).body(error);
    }
}
