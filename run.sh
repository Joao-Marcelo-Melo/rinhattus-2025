#!/usr/bin/env bash
set -euo pipefail

# =========================
# Configs com defaults
# =========================
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.yml}"   # seu compose
K6_DIR="${K6_DIR:-k6}"
K6_SCRIPT="${K6_SCRIPT:-script.js}"
PARTICIPANTE="${PARTICIPANTE:-anonimo}"
HEALTH_URL="${HEALTH_URL:-http://localhost:9001/dividas}"
MAX_TRIES="${MAX_TRIES:-10}"
SLEEP_SECS="${SLEEP_SECS:-5}"

# =========================
# Helpers
# =========================
log(){ printf "\033[1;34m[run]\033[0m %s\n" "$*"; }
err(){ printf "\033[1;31m[err]\033[0m %s\n" "$*" >&2; }

need(){ command -v "$1" >/dev/null 2>&1 || { err "Tool '$1' não encontrado no PATH"; exit 1; }; }

# =========================
# Checks básicos
# =========================
need docker
need bash
need ./mvnw
need curl
need k6

if [[ ! -f "$COMPOSE_FILE" ]]; then
  err "Compose file '$COMPOSE_FILE' não encontrado."
  exit 1
fi
if [[ ! -d "$K6_DIR" || ! -f "$K6_DIR/$K6_SCRIPT" ]]; then
  err "Script K6 '$K6_DIR/$K6_SCRIPT' não encontrado."
  exit 1
fi

# =========================
# Down, build, up
# =========================
log "Derrubando containers (compose: $COMPOSE_FILE)..."
#sudo docker compose --compatibility -f "$COMPOSE_FILE" down -v --remove-orphans || true

log "Build Maven (package)..."
#./mvnw clean package -DskipTests

log "Subindo containers com build..."
sudo docker compose --compatibility -f "$COMPOSE_FILE" up -d --build

# =========================
# Espera healthcheck
# =========================
log "Esperando serviço responder em: $HEALTH_URL"
try=1
until curl -fsS --max-time 2 "$HEALTH_URL" >/dev/null 2>&1; do
  if [[ $try -ge $MAX_TRIES ]]; then
    err "Timeout de readiness. Verifique 'docker compose logs -f'."
    exit 1
  fi
  log "Ainda iniciando... tentativa $try/$MAX_TRIES"
  sleep "$SLEEP_SECS"
  try=$((try+1))
done
log "Servidor respondeu. Seguimos."

# =========================
# Rodar K6
# =========================
log "Criando pasta de resultados esperada pelo script da rinha..."
mkdir -p "participantes/$PARTICIPANTE" || true
# também cria o caminho que alguns scripts esperam quando rodados dentro de /k6
mkdir -p "$K6_DIR/../participantes/$PARTICIPANTE" || true

log "Rodando K6 (PARTICIPANTE=$PARTICIPANTE)..."
pushd "$K6_DIR" >/dev/null
k6 run -e PARTICIPANTE="$PARTICIPANTE" -e PARTICIPANT="$PARTICIPANTE" "$K6_SCRIPT" || true
popd >/dev/null

# =========================
# Mostrar resultado
# =========================
RESULT_LOCAL="$K6_DIR/resultado.json"
RESULT_PARTICIPANTE="participantes/$PARTICIPANTE/resultado.json"

if [[ -f "$RESULT_PARTICIPANTE" ]]; then
  log "Resultado (participantes): $RESULT_PARTICIPANTE"
  cat "$RESULT_PARTICIPANTE" | sed -n '1,80p'
elif [[ -f "$RESULT_LOCAL" ]]; then
  log "Resultado (local): $RESULT_LOCAL"
  cat "$RESULT_LOCAL" | sed -n '1,80p'
else
  err "Nenhum resultado.json encontrado. Veja logs do K6."
fi

log "Fim."
