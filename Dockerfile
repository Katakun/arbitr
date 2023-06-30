FROM openjdk:17
HEALTHCHECK --interval=2s --timeout=10s --retries=10 CMD curl http://localhost:8080/actuator/health/liveness || exit 1
WORKDIR /app
COPY /target/arbitr-0.0.1-SNAPSHOT.jar /app
RUN mkdir share
ENTRYPOINT ["java", "-jar", "arbitr-0.0.1-SNAPSHOT.jar"]
