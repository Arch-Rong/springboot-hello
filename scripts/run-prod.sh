#!/usr/bin/env bash
# 用 prod profile 启动（连 Docker Postgres :5433）
set -euo pipefail
cd "$(dirname "$0")/.."

if ! docker compose ps --status running 2>/dev/null | grep -q springboot-hello-postgres; then
  echo "Postgres 未运行，正在启动..."
  docker compose up -d
  sleep 2
fi

# 若存在 .env 则导出（Spring Boot 不会自动读 .env）
if [[ -f .env ]]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"
exec mvn spring-boot:run
