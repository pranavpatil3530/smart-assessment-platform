#!/usr/bin/env bash
set -e

echo "=== [Render Build Phase: Native Environment] ==="

# Download portable OpenJDK 17 for Linux if not present
if [ ! -d "./jdk/bin" ]; then
  echo "Downloading OpenJDK 17 Linux runtime..."
  mkdir -p ./jdk
  curl -sL "https://api.adoptium.net/v3/binary/latest/17/ga/linux/x64/jdk/hotspot/normal/eclipse" -o jdk.tar.gz
  echo "Extracting OpenJDK 17..."
  tar -xzf jdk.tar.gz -C ./jdk --strip-components=1
  rm -f jdk.tar.gz
  echo "OpenJDK 17 successfully installed."
fi

export JAVA_HOME="$PWD/jdk"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Using Java version:"
"$JAVA_HOME/bin/java" -version

# Ensure mvnw is executable
chmod +x ./mvnw

echo "Packaging Spring Boot executable JAR..."
./mvnw clean package -DskipTests

echo "=== [Build Complete: Artifact Ready in target/] ==="
