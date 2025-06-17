# 1. Gradle로 빌드 (Kotlin DSL 사용)
FROM gradle:8.7-jdk21 AS build
WORKDIR /build
COPY . .

# gradlew 실행 권한 부여 (중요)
RUN chmod +x ./gradlew

# 프로젝트에서 설정된 gradle wrapper로 빌드
RUN ./gradlew build -x test

# 2. 실행 이미지
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /build/build/libs/build-0.0.1-SNAPSHOT.jar app.jar
ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]