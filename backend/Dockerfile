# 1. Maven으로 빌드
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# 2. 실행 이미지
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]