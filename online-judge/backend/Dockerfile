FROM maven:3.8.1-jdk-8-slim AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=builder /app/target/yuoj-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8121
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
