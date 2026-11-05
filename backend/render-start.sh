#!/usr/bin/env bash
set -e

echo "=== [Render Start Phase] ==="

export JAVA_HOME="$PWD/jdk"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Launching Smart Assessment Platform on Port: ${PORT:-8080}..."
exec "$JAVA_HOME/bin/java" \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -Dserver.port="${PORT:-8080}" \
  -jar target/smart-assessment-platform-1.0.0.jar
