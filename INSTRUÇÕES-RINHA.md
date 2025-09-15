# 1ª Edição da Rinhattus - 2025

## O Desafio
Você deverá criar uma **API** para gerenciar o recebimento de dívidas enviadas por um **Sistema de Dívida Ativa - SDA**.

Sua **API** deverá expor os seguintes endpoints:

- `POST /dividas` → recebe e registra uma nova dívida.
- `GET /dividas` → consulta as dívidas dentro de um intervalo de tempo.

Se sua **API** não conseguir processar uma dívida, deverá responder com **HTTP 500** e o seguinte corpo:
```json
{
  "status": 500,
  "mensagem": "Não foi possível realizar o recebimento da dívida"
}
```

---

## Arquitetura e Restrições

Sua API deverá ser entregue seguindo as regras abaixo:

1. **Entrega via Docker Compose**
    - Todos os serviços devem estar declarados em `docker-compose.yml`.
    - No mínimo **2 instâncias** da API devem ser disponibilizadas.
    - É obrigatório o uso de **load balancer** (NGINX, HAProxy, Ou crie o seu 😙).
    - Todas as imagens usadas devem estar públicas no [Docker Hub](https://hub.docker.com/).

2. **Configuração de Porta**
    - Todos os endpoints devem estar disponíveis em `http://localhost:9001`.

3. **Limites de Recursos**
    - Uso máximo de **1,5 CPU** e **350MB de memória** somados entre todos os serviços.
    - Deve-se configurar com `deploy.resources.limits.cpus` e `deploy.resources.limits.memory`.
   ```yml
   services:
     nginx:
       deploy:
         resources:
           limits:
             cpus: "0.15"
             memory: "42MB"
   ```

4. **Restrições de Execução**
    - Java 21
    - Spring Boot 3.5.5
    - O modo de rede deve ser **bridge** (não usar host).
    - Não é permitido `privileged` mode.
    - Não é permitido uso de serviços replicados (`deploy.replicas`).
    - Não há necessidade de autenticação.

---

## Pontuação
A pontuação será calculada com base em performance e consistência.

### P99
- Consideraremos o **1% mais lento** das respostas.
- A partir de 15ms, cada 1ms abaixo de 16ms gera **+2%** de bonificação sobre o valor total líquido.
- Fórmula:
  ```
  Math.floor((16 - p99) * 0.02)
  ```

**Exemplos:**
- P99 = 15ms → +2%
- P99 = 12ms → +8%
- P99 = 10ms → +12%

### Penalizações
1. **Falhas não controladas** → erro diferente do definido resulta em **-8%** do valor bruto.
2. **Excesso de falhas controladas** → tolerado até **20%** de erros controlados. Acima disso, aplica-se **-15%** de multa do valor bruto.

### Fórmula Final
```
totalMultas = (valorBruto * percentualMultaFalhasNaoControladas) + (valorBruto * percentualMultaFalhasControladas)
valorLiquido = valorBruto - totalMultas
Pontuação Final = valorLiquido * bônusP99
```

---

## Detalhamento dos Endpoints

### `POST /dividas`
**Request Body**
```json
{
  "identificador": "4a7901b8-7d26-4d9d-aa19-4dc1c7cf60b3",
  "valor": 19.90
}
```
- `identificador`: obrigatório, UUID único.
- `valor`: obrigatório, decimal.

**Response (sucesso)**
```json
{
  "status": 200,
  "mensagem": "Dívida registrada com sucesso"
}
```
> A mensagem não será validada, mas recomenda-se manter uma resposta consistente.

---

### `GET /dividas?from=2020-07-10T12:34:56.000Z&to=2020-07-10T12:35:56.000Z`
**Response**
```json
{
  "quantidadeTotal": 5000,
  "valorTotal": 3456.78
}
```
- `from`: timestamp ISO UTC (inclusivo).
- `to`: timestamp ISO UTC (exclusivo).
- `quantidadeTotal`: inteiro obrigatório.
- `valorTotal`: decimal obrigatório.

---

## Testes

### Como testar localmente
1. Suba os containers:
   ```bash
   docker compose up -d
   ```

2. Execute o teste com K6:
    - Instale o [K6](https://grafana.com/docs/k6/latest/set-up/install-k6/).
    - Baixe o arquivo `script.js` na pasta `/k6`.
    - Execute:
      ```bash
      k6 run script.js
      ```

3. O resultado será salvo em `resultado.json`.

---

## Submissão
Você deverá abrir um **Pull Request** ou **Merge Request** neste repositório em uma branch com seu nome, criando um diretório dentro de `participantes/` também com seu nome. Inclua:

- `README.md` → descrevendo tecnologias usadas, instruções de execução e informações adicionais e link do projeto no git.
- `docker-compose.yml` → com todas as imagens usadas.
- Arquivos de configuração adicionais necessários para suas imagens.

> Veja [exemplo de envio](https://git.eloware.com.br/attus/rinhattus-2025-1/-/tree/main/participantes/attus?ref_type=heads).

**Prazo final:** *11/09 - 23h59 (Horário de Brasília)*   
*O resultado será divulgado no dia 12/09 no evento **Sprint Review***

### Observações
- Não é necessário enviar o código-fonte.
- Caso queira reenviar ou ajustar sua API, faça um novo PR/MR removendo o `resultado.json`.
- Fique atento ao prazo informado pela organização.


### Premiação
1° Colocado: `R$1000`    
2° Colocado: `R$500`   
3° Colocado: `R$200`   
*Obs¹: Os prêmios serão pagos com itens no valor da moeda corrente.*    
*Obs²: Em caso de empate, será usado o **P95** e total de **ERROS** como critério de desempate.*