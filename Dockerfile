# Build Stage
FROM gradle:9.6-jdk17-alpine AS builder

WORKDIR /app

# Gradle 캐싱을 위한 의존성 파일 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 다운로드 (캐싱 레이어)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드 (테스트 스킵)
RUN gradle clean build -x test --no-daemon

# Runtime Stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 시스템 패키지 업데이트 및 필수 도구 설치
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    wget \
    && rm -rf /var/lib/apt/lists/*

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 비루트 사용자 생성 및 권한 설정
RUN addgroup --system spring && \
    adduser --system --group spring && \
    chown -R spring:spring /app

USER spring:spring

# 포트 노출
EXPOSE 8080

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM 옵션 설정
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]