FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml checkstyle.xml ./
COPY config-contracts config-contracts
COPY client-library client-library
COPY config-service config-service
COPY springexample1 springexample1
RUN mvn -B -pl springexample1 -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/* \
 && useradd --system --create-home --uid 10001 appuser
COPY --from=builder /build/springexample1/target/springexample1-*.jar app.jar
USER appuser
EXPOSE 8085
HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=5 \
  CMD ["sh", "-c", "curl -fsS http://localhost:8085/actuator/health || exit 1"]
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
