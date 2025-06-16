# 1. Gradle로 빌드 (Kotlin DSL 사용)
FROM gradle:8.7-jdk21 AS build
WORKDIR /build
COPY --chown=gradle:gradle . .
RUN gradle build -x test

# 2. 실행 이미지
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /build/build/libs/*.jar app.jar
ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]