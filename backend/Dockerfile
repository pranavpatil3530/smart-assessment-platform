# ----------------------------------------------------
# Stage 1: Build the Spring Boot JAR with Maven & OpenJDK 17
# ----------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and compile fat JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ----------------------------------------------------
# Stage 2: Minimal, secure JRE Alpine runtime for Render
# ----------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Non-root user for cloud security best-practices
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy executable jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Render assigns dynamic PORT environment variable (typically 10000 or 8080)
ENV PORT=8080
EXPOSE 8080 10000

# Optimize JVM memory allocation inside cloud containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
