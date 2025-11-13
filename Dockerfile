# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src src
RUN mvn -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd -u 1001 spring
COPY --from=build /workspace/target/tmc-secure-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
USER spring
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app/app.jar"]