package com.jmz.rinha.controller;

import com.jmz.rinha.model.CreateDividaRequest;
import com.jmz.rinha.model.DividasPeriodoResponse;
import com.jmz.rinha.service.DividaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/dividas")
public class DividaController {

    private final DividaService service;

    public DividaController(DividaService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> criar(@RequestBody CreateDividaRequest req) {
        return service.save(req)
                .thenReturn(ResponseEntity.ok("{\"status\":200,\"mensagem\":\"Dívida registrada com sucesso\"}"))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"status\":500,\"mensagem\":\"Não foi possível realizar o recebimento da dívida\"}"));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<DividasPeriodoResponse>> consultar(
            @RequestParam("from") String fromStr,
            @RequestParam("to") String toStr) {
        try {
            LocalDateTime from = LocalDateTime.parse(fromStr.replace("Z", ""));
            LocalDateTime to = LocalDateTime.parse(toStr.replace("Z", ""));
            return service.findByPeriod(from, to).map(ResponseEntity::ok);
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }
}
