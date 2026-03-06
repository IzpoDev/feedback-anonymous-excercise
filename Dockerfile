# ETAPA 1: Construcción (Java 21)
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Java 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 8082
ENTRYPOINT ["java", "-Xmx300m", "-jar", "app.jar"]