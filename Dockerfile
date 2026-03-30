# Bước 1: Dùng Maven để build project ra file .jar
FROM maven:3.8.5-openjdk-22 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Bước 2: Dùng bản Java nhẹ để chạy file .jar vừa build
FROM openjdk:22-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]