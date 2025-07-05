FROM gradle:8.14.2-jdk21-alpine AS builder
WORKDIR /app
COPY gradle gradle/
COPY settings.gradle.kts gradle.properties ./
COPY individuals/ individuals/
WORKDIR /app/individuals
RUN gradle :individuals:clean :individuals:bootJar

FROM eclipse-temurin:21-alpine
WORKDIR /home/individuals
EXPOSE 8080
COPY --from=builder /app/individuals/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]