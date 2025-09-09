package com.jmz.rinha.controller;

import com.jmz.rinha.service.DividaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/dividas")
public class DividaController {

    private final DividaService service;

    public DividaController(DividaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam("from") String from,
                                    @RequestParam("to") String to) {
        try {
            Instant iFrom = Instant.parse(from);
            Instant iTo = Instant.parse(to);
            var resumo = service.resumo(iFrom, iTo);
            return ResponseEntity.ok(Map.of(
                    "quantidadeTotal", resumo.quantidadeTotal(),
                    "valorTotal", resumo.valorTotal()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", 500, "mensagem",
                            "Não foi possível realizar o recebimento da dívida"));
        }
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> body) {
        try {
            UUID identificador = UUID.fromString(body.get("identificador").toString());
            BigDecimal valor = new BigDecimal(body.get("valor").toString());
            service.registrar(identificador, valor);return ResponseEntity.ok(Map.of("status", 200,
                    "mensagem", "Dívida registrada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", 500, "mensagem",
                            "Não foi possível realizar o recebimento da dívida"));
        }
    }
}
