FROM openjdk:17
WORKDIR /app
COPY /target/arbitr-0.0.1-SNAPSHOT.jar /app
RUN mkdir share
ENTRYPOINT ["java", "-jar", "arbitr-0.0.1-SNAPSHOT.jar"]