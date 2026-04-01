# Bước 1: Dùng Maven (kèm Eclipse Temurin Java 22) để build project
FROM maven:3.9.6-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Bước 2: Dùng Eclipse Temurin Java 22 để chạy ứng dụng
FROM eclipse-temurin:22-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT java -jar app.jar --server.port=${PORT:-8080}