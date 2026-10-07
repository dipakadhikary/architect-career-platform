FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /src

COPY pom.xml .
COPY config ./config
COPY src ./src

RUN mvn -B -DskipTests -Dspotless.check.skip=true package

FROM eclipse-temurin:21-jre-jammy

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --create-home --uid 10001 --shell /usr/sbin/nologin acos

WORKDIR /app

COPY --from=build /src/target/architect-career-operating-system-*.jar /app/app.jar

USER acos

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=5 \
  CMD curl -fsS http://127.0.0.1:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
