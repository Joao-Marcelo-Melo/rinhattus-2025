import { sleep } from 'k6';
import { Counter } from "k6/metrics";
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.2/index.js'; import { Httpx } from 'https://jslib.k6.io/httpx/0.1.0/index.js';
import { uuidv4 } from "https://jslib.k6.io/k6-utils/1.4.0/index.js";

const MAX_REQUESTS = __ENV.MAX_REQUESTS ?? 500;
const PARTICIPANTE = __ENV.PARTICIPANTE ?? 'Anônimo';

export const options = {
    summaryTrendStats: [
        "p(99)",
        "p(95)",
        "count",
    ],
    scenarios: {
        dividas: {
            exec: 'dividas',
            executor: "ramping-vus",
            startVUs: 1,
            gracefulRampDown: "0s",
            stages: [{ target: MAX_REQUESTS, duration: "60s" }]
        }
    }
}

const dividasEnviadasSdaCounter = new Counter("dividas_enviadas_sda");
const transactionsSuccessCounter = new Counter("transactions_success");
const transactionsUncontrolledFailureCounter = new Counter("transactions_uncontrolled_failure");
const transactionsControlledFailureCounter = new Counter("transactions_controlled_failure");
const totalTransactionsAmountCounter = new Counter("total_transactions_amount");

const backendHttp = new Httpx({
    baseURL: "http://localhost:9001",
    headers: {
        "Content-Type": "application/json",
    },
    timeout: 1500,
});

export async function dividas() {
    const payload = {
        identificador: uuidv4(),
        valor: 115.45
    };

    let response = await backendHttp.asyncRequest('POST', '/dividas', JSON.stringify(payload));

    try {
        response = JSON.parse(response.body);
        if (response.status == 200) {
            transactionsSuccessCounter.add(1);
            transactionsUncontrolledFailureCounter.add(0);
            transactionsControlledFailureCounter.add(0);
        }
        else {
            transactionsSuccessCounter.add(0);

            if (response.mensagem == 'Não foi possível realizar o recebimento da dívida') {
                transactionsControlledFailureCounter.add(1);
                transactionsUncontrolledFailureCounter.add(0);
            } else {
                transactionsControlledFailureCounter.add(0);
                transactionsUncontrolledFailureCounter.add(1);
            }
        }
    } catch (e) {
        transactionsSuccessCounter.add(0);
        transactionsUncontrolledFailureCounter.add(1);
        transactionsControlledFailureCounter.add(0);
    }

    dividasEnviadasSdaCounter.add(1);

    sleep(1);
}

export async function teardown(data) {
    const to = new Date();
    const from = new Date(to.getTime() - 70 * 1000);

    console.info(`Resumo de ${from.toISOString()} até ${to.toISOString()}`);

    let response = await backendHttp.asyncRequest('GET', `/dividas?from=${from.toISOString()}&to=${to.toISOString()}`);
    response = JSON.parse(response.body);

    totalTransactionsAmountCounter.add(response.valorTotal);
}

export async function handleSummary(data) {
    const P99 = data.metrics['http_req_duration{expected_response:true}'].values['p(99)'];
    const P95 = data.metrics['http_req_duration{expected_response:true}'].values['p(95)'];
    const valorPorcentagemBonusP99 = Math.max(Math.floor((16.0 - P99) * 2), 0);
    let valorRealMulta = 0;

    const valorTotalBruto = data.metrics.total_transactions_amount.values.count;

    if (data.metrics.transactions_uncontrolled_failure.values.count > 0) {
        valorRealMulta = valorRealMulta + (valorTotalBruto * 0.08);
    }

    if ((data.metrics.transactions_controlled_failure.values.count / data.metrics.dividas_enviadas_sda.values.count) > 0.2) {
        valorRealMulta = valorRealMulta + (valorTotalBruto * 0.15);
    }

    let valorTotalLiquido = valorTotalBruto - valorRealMulta;
    valorTotalLiquido = valorTotalLiquido * (1 + (valorPorcentagemBonusP99 / 100));

    const resultado = {
        stdout: textSummary(data),
    };

    const resultadoJson = {
        participante: PARTICIPANTE,
        total_bruto: valorTotalBruto.toFixed(2),
        total_liquido: valorTotalLiquido.toFixed(2),
        p99: {
            valor: `${P99.toFixed(2)}ms`,
            bonus: `${valorPorcentagemBonusP99}%`,
        },
        multas: {
            valorReal: valorRealMulta.toFixed(2),
            errosInesperados: {
                quantidade: data.metrics.transactions_uncontrolled_failure.values.count,
                multa: `${data.metrics.transactions_uncontrolled_failure.values.count > 0 ? 8 : 0}%`
            },
            errosControlados: {
                quantidade: data.metrics.transactions_controlled_failure.values.count,
                multa: `${(data.metrics.transactions_controlled_failure.values.count / data.metrics.dividas_enviadas_sda.values.count) > 0.2 ? 15 : 0}%`
            }
        },
        desempate: {
            p95: `${P95.toFixed(2)}ms`,
            totalErros: data.metrics.transactions_uncontrolled_failure.values.count + data.metrics.transactions_controlled_failure.values.count
        }
    }

    let nomeArquivo = `../participantes/${PARTICIPANTE}/resultado.json`

    if (PARTICIPANTE === 'Anônimo') {
        nomeArquivo = `./resultado.json`
    }

    resultado[nomeArquivo] = JSON.stringify(resultadoJson, null, 2);

    return resultado;
}