#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
LOG_DIR="${MERCATO_LOG_DIR:-/private/tmp/mercato-local-logs}"
START_FRONTEND="${START_FRONTEND:-true}"

if [[ -f "$BACKEND_DIR/.local-env" ]]; then
  # shellcheck disable=SC1091
  source "$BACKEND_DIR/.local-env"
fi

mkdir -p "$LOG_DIR"

declare -a CHILD_PIDS=()
LAST_PID=""

cleanup() {
  local exit_code=$?
  trap - EXIT INT TERM
  if ((${#CHILD_PIDS[@]})); then
    echo
    echo "Stopping Mercato services..."
    kill "${CHILD_PIDS[@]}" 2>/dev/null || true
    wait "${CHILD_PIDS[@]}" 2>/dev/null || true
  fi
  exit "$exit_code"
}

trap cleanup EXIT INT TERM

port_is_open() {
  lsof -tiTCP:"$1" -sTCP:LISTEN >/dev/null 2>&1
}

require_free_port() {
  local port="$1"
  local name="$2"
  if port_is_open "$port"; then
    echo "Port $port is already in use; cannot start $name." >&2
    exit 1
  fi
}

start_jar() {
  local name="$1"
  local jar="$2"

  if [[ ! -f "$jar" ]]; then
    echo "Missing build for $name: $jar" >&2
    echo "Run the Maven package command for that module first." >&2
    exit 1
  fi

  echo "Starting $name..."
  java -jar "$jar" >"$LOG_DIR/$name.log" 2>&1 &
  local pid=$!
  CHILD_PIDS+=("$pid")
  LAST_PID="$pid"
  printf '%s\n' "$pid" >"$LOG_DIR/$name.pid"
}

wait_for_http() {
  local name="$1"
  local url="$2"
  local pid="$3"
  local log_key
  log_key="$(printf '%s' "${name%% *}" | tr '[:upper:]' '[:lower:]')"

  for _ in {1..90}; do
    if curl -fsS --max-time 1 -o /dev/null "$url" 2>/dev/null; then
      echo "  $name is ready"
      return 0
    fi
    if ! kill -0 "$pid" 2>/dev/null; then
      echo "$name stopped during startup. Last log lines:" >&2
      tail -40 "$LOG_DIR/$log_key.log" >&2 || true
      return 1
    fi
    sleep 1
  done

  echo "$name did not become ready at $url" >&2
  tail -40 "$LOG_DIR/$log_key.log" >&2 || true
  return 1
}

wait_for_port() {
  local name="$1"
  local port="$2"
  local pid="$3"
  local log_key
  log_key="$(printf '%s' "${name%% *}" | tr '[:upper:]' '[:lower:]')"

  for _ in {1..90}; do
    if port_is_open "$port"; then
      echo "  $name is ready on $port"
      return 0
    fi
    if ! kill -0 "$pid" 2>/dev/null; then
      echo "$name stopped during startup. Last log lines:" >&2
      tail -40 "$LOG_DIR/$log_key.log" >&2 || true
      return 1
    fi
    sleep 1
  done

  echo "$name did not bind to port $port" >&2
  tail -40 "$LOG_DIR/$log_key.log" >&2 || true
  return 1
}

wait_for_eureka() {
  local app_name="$1"

  for _ in {1..60}; do
    if curl -fsS --max-time 1 \
      -H "Accept: application/json" \
      "http://localhost:8761/eureka/apps/$app_name" >/dev/null; then
      echo "  $app_name is registered"
      return 0
    fi
    sleep 1
  done

  echo "$app_name did not register with Discovery Server" >&2
  return 1
}

for port_and_name in \
  "8761 Discovery Server" \
  "8888 Config Server" \
  "8081 Auth Service" \
  "8082 Admin Service" \
  "8083 Customer Service" \
  "8084 Seller Service" \
  "8085 Product Service" \
  "8086 Cart Service" \
  "8087 Wishlist Service" \
  "8080 API Gateway"; do
  port="${port_and_name%% *}"
  name="${port_and_name#* }"
  require_free_port "$port" "$name"
done

if [[ "$START_FRONTEND" == "true" ]]; then
  require_free_port 5173 "Storefront"
fi

if command -v mysqladmin >/dev/null 2>&1; then
  if ! MYSQL_PWD="${AUTH_DB_PASSWORD:-rishabh2506}" \
    mysqladmin -u "${AUTH_DB_USERNAME:-root}" ping --silent; then
    echo "MySQL is not reachable. Start MySQL before Mercato." >&2
    exit 1
  fi
fi

start_jar "discovery" \
  "$BACKEND_DIR/discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar"
wait_for_http "Discovery Server" "http://localhost:8761/" "$LAST_PID"

start_jar "config" \
  "$BACKEND_DIR/config-server/target/config-server-0.0.1-SNAPSHOT.jar"
wait_for_http "Config Server" \
  "http://localhost:8888/auth-service/default" "$LAST_PID"

start_jar "auth" \
  "$BACKEND_DIR/services/auth-service/target/auth-service-0.0.1-SNAPSHOT.jar"
auth_pid="$LAST_PID"

start_jar "admin" \
  "$BACKEND_DIR/services/admin-service/target/admin-service-0.0.1-SNAPSHOT.jar"
admin_pid="$LAST_PID"

start_jar "customer" \
  "$BACKEND_DIR/services/customer-service/target/customer-service-0.0.1-SNAPSHOT.jar"
customer_pid="$LAST_PID"

start_jar "seller" \
  "$BACKEND_DIR/services/seller-service/target/seller-service-0.0.1-SNAPSHOT.jar"
seller_pid="$LAST_PID"

start_jar "product" \
  "$BACKEND_DIR/services/product-service/target/productservicedatabase-0.0.1-SNAPSHOT.jar"
product_pid="$LAST_PID"

start_jar "cart" \
  "$BACKEND_DIR/services/cart-service/target/cart-service-0.0.1-SNAPSHOT.jar"
cart_pid="$LAST_PID"

start_jar "wishlist" \
  "$BACKEND_DIR/services/wishlist-service/target/wishlist-service-0.0.1-SNAPSHOT.jar"
wishlist_pid="$LAST_PID"

wait_for_port "Auth Service" 8081 "$auth_pid"
wait_for_port "Admin Service" 8082 "$admin_pid"
wait_for_port "Customer Service" 8083 "$customer_pid"
wait_for_port "Seller Service" 8084 "$seller_pid"
wait_for_port "Product Service" 8085 "$product_pid"
wait_for_port "Cart Service" 8086 "$cart_pid"
wait_for_port "Wishlist Service" 8087 "$wishlist_pid"

# The Gateway takes its initial load-balancer snapshot at startup. Waiting for
# every business service to register avoids a temporary wave of 503 responses.
for app_name in \
  AUTH-SERVICE \
  ADMIN-SERVICE \
  CUSTOMER-SERVICE \
  SELLER-SERVICE \
  PRODUCT-SERVICE \
  CART-SERVICE \
  WISHLIST-SERVICE; do
  wait_for_eureka "$app_name"
done

start_jar "gateway" \
  "$BACKEND_DIR/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar"
wait_for_port "API Gateway" 8080 "$LAST_PID"
# Give the Gateway's discovery client one short refresh window before the first
# routed readiness probe.
sleep 6
wait_for_http "API Gateway" "http://localhost:8080/products" "$LAST_PID"

if [[ "$START_FRONTEND" == "true" ]]; then
  echo "Starting Storefront..."
  (
    cd "$BACKEND_DIR/storefront"
    npm run dev -- --host 127.0.0.1
  ) >"$LOG_DIR/storefront.log" 2>&1 &
  frontend_pid=$!
  CHILD_PIDS+=("$frontend_pid")
  printf '%s\n' "$frontend_pid" >"$LOG_DIR/storefront.pid"
  wait_for_http "Storefront" "http://127.0.0.1:5173/" "$frontend_pid"
fi

echo
echo "Mercato is running."
echo "  Storefront:       http://127.0.0.1:5173"
echo "  API Gateway:      http://localhost:8080"
echo "  Discovery Server: http://localhost:8761"
echo "  Logs:             $LOG_DIR"
echo
echo "Press Ctrl+C to stop the stack."

wait
