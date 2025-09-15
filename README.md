# 🥇 Rinha de Backend 2025 – Implementação Vencedora

Este repositório contém a minha solução **campeã** da **1ª Edição da Rinha de Backend – 2025**,  
um desafio **interno da minha empresa**, proposto entre os programadores para testar habilidades de arquitetura,  
otimização e resiliência em sistemas de alta carga.

Eu fui o **grande campeão da competição**, alcançando o melhor desempenho geral. 🚀

---

## 🚀 Destaques da Solução

- ⚡ **Performance extrema**: latência consistente abaixo de 2ms
- 🧠 **Banco off-heap** com `UnsafeBuffer` (Agrona) para operações rápidas de soma e consulta
- 🧩 **Arquitetura minimalista**: apenas o essencial para máxima eficiência
- 🔧 **Execução simplificada** com [`run.sh`](./run.sh)
- 🛡️ **Zero erros** durante toda a competição

---

## 📊 Resultados Oficiais

Minha implementação ficou em **1º lugar no ranking oficial**, com os seguintes números:

```json
{
  "participante": "João Marcelo de Melo Bomfim",
  "total_bruto": "1758188.05",
  "total_liquido": "2268062.58",
  "p99": {
    "valor": "1.38ms",
    "bonus": "29%"
  },
  "multas": {
    "valorReal": "0.00",
    "errosInesperados": {
      "quantidade": 0,
      "multa": "0%"
    },
    "errosControlados": {
      "quantidade": 0,
      "multa": "0%"
    }
  },
  "desempate": {
    "p95": "1.05ms",
    "totalErros": 0
  }
}
```

✅ **Zero erros**  
✅ **Latência consistente** (`p95: 1.05ms`, `p99: 1.38ms`)  
✅ **Campeão absoluto da edição**

---

## 📖 Especificações da Rinha

As regras e instruções oficiais estão documentadas em [`instruções.md`](./instruções.md), arquivo disponibilizado pela organização da competição.

---

## ⚙️ Como Executar

### 1. Usando `run.sh` (recomendado)

O script `run.sh` já contém todos os passos para buildar e executar a aplicação:

```bash
./run.sh
```

### 2. Execução manual com Maven + Docker

Caso prefira, é possível compilar e subir os containers manualmente:

```bash
./mvnw clean package -DskipTests
docker compose up --build
```

A API ficará disponível em:

```
http://localhost:8080/dividas
```

---

## 📌 Endpoints

- **POST /dividas** → Registra uma nova dívida
- **GET /dividas** → Consulta dívidas em um intervalo de tempo

---

## 🌌 Créditos

- Autor: **João Marcelo de Melo Bomfim**
- Competição: rinhattus - 2025
- Resultado: **🥇 Campeão da edição**

---