package com.jmz.rinha.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handle(Exception ex) {
        return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "mensagem", "Não foi possível realizar o recebimento da dívida"
        ));
    }
}
