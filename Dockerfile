# syntax=docker/dockerfile:1

############################
# 1) Build Stage (Gradle)
############################
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradle 래퍼/설정 먼저 복사(캐시 최적화)
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle
RUN chmod +x gradlew

# 의존성 캐시 예열
RUN ./gradlew --no-daemon -q dependencies || true

# 소스 복사 후 빌드
COPY src src
# Spring Boot면 bootJar, 아니면 build
RUN ./gradlew --no-daemon -q bootJar -x test || ./gradlew --no-daemon -q build -x test

############################
# 2) Runtime Stage (JRE)
############################
FROM eclipse-temurin:21-jre
WORKDIR /app

# build/libs 아래 생성된 JAR 하나를 app.jar로 배치
COPY --from=build /app/build/libs/*.jar /app/app.jar

ENV TZ=Asia/Seoul \
    JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
