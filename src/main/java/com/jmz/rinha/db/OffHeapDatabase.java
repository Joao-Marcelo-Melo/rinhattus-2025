package com.jmz.rinha.db;

import com.jmz.rinha.model.ResultadoConsulta;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.time.Instant;

public class OffHeapDatabase {
    private static final int BUCKET_SIZE = Long.BYTES + Long.BYTES;

    private final UnsafeBuffer buffer;
    private final int bucketRange;

    public OffHeapDatabase(int secondsRange) {
        this.bucketRange = secondsRange;
        final ByteBuffer direct = ByteBuffer.allocateDirect(secondsRange * BUCKET_SIZE);
        this.buffer = new UnsafeBuffer(direct);
    }

    public void salvar(double valor, Instant criadoEm) {
        final int idx = toIndex(criadoEm);
        final int valIdx = idx + Long.BYTES;

        while (true) {
            final long atual = buffer.getLongVolatile(idx);
            if (buffer.compareAndSetLong(idx, atual, atual + 1)) {
                break;
            }
        }

        while (true) {
            final long raw = buffer.getLongVolatile(valIdx);
            final double atual = Double.longBitsToDouble(raw);
            final double novo = atual + valor;
            final long newRaw = Double.doubleToRawLongBits(novo);
            if (buffer.compareAndSetLong(valIdx, raw, newRaw)) {
                break;
            }
        }
    }

    public ResultadoConsulta consultar(Instant from, Instant to) {
        long totalCount = 0L;
        double totalValor = 0.0;

        final long start = from.getEpochSecond();
        final long end   = to.getEpochSecond();

        for (long s = start; s <= end; s++) {
            final int idx    = (int) (s % bucketRange) * BUCKET_SIZE;
            final int valIdx = idx + Long.BYTES;

            final long count = buffer.getLongVolatile(idx);
            final long raw   = buffer.getLongVolatile(valIdx);
            final double valor = Double.longBitsToDouble(raw);

            totalCount += count;
            totalValor += valor;
        }

        return new ResultadoConsulta(totalCount, totalValor);
    }

    public void limpar() {
        buffer.setMemory(0, buffer.capacity(), (byte) 0);
    }

    private int toIndex(Instant instant) {
        final long second = instant.getEpochSecond() % bucketRange;
        return (int) second * BUCKET_SIZE;
    }
}
