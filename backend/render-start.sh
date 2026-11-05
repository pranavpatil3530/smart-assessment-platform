#!/usr/bin/env bash
set -e

# Auto-detect if running from root repository directory
if [ -d "backend" ]; then
  echo "Navigating into backend directory..."
  cd backend
fi

echo "=== [Render Start Phase] ==="

export JAVA_HOME="$PWD/jdk"
export PATH="$JAVA_HOME/bin:$PATH"

JAR_FILE=$(find target -name "*.jar" ! -name "*original*" ! -name "*sources*" | head -n 1)

if [ -z "$JAR_FILE" ]; then
  echo "ERROR: Executable JAR file not found in target/. Check build output."
  exit 1
fi

echo "Launching Smart Assessment Platform from $JAR_FILE on Port: ${PORT:-8080}..."
exec "$JAVA_HOME/bin/java" \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -Dserver.port="${PORT:-8080}" \
  -jar "$JAR_FILE"
