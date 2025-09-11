package com.jmz.rinha.db;

import com.jmz.rinha.model.ResultadoConsulta;

import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

class Bucket {
    final LongAdder quantidade = new LongAdder();
    final DoubleAdder valor = new DoubleAdder();

    void add(double v) {
        quantidade.increment();
        valor.add(v);
    }

    ResultadoConsulta toResultado() {
        return new ResultadoConsulta(quantidade.sum(), valor.sum());
    }
}
