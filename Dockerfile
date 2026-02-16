# =========================
# Build Stage
# =========================
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# 프로젝트 전체 복사
COPY . .

#./gradlew clean bootJar → OK, Gradle Wrapper가 빌드에 필요 모든 것을 다운로드함
#gradle clean build → 에러, 시스템 Gradle이 없으면 not found

# Gradle Wrapper 권한 부여 후 빌드 (테스트 제외)
RUN chmod +x ./gradlew && \
    ./gradlew clean bootJar -x test

# =========================
# Runtime Stage
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app


# 필수 도구 설치 (헬스체크용 wget)
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        wget \
    && rm -rf /var/lib/apt/lists/*

# 타임존 설정 (선택)
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# JVM 옵션 환경변수 (필요에 따라 조절 가능)
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 헬스체크: /actuator/health 호출
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

    # 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]