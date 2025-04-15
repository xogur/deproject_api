# ✅ JDK 21을 기반으로
FROM openjdk:21-jdk-slim

# ✅ 작업 디렉토리 설정
WORKDIR /app

# ✅ build한 JAR 파일 복사 (경로는 실제 jar 이름에 따라 달라질 수 있음)
COPY build/libs/deproject_api-0.0.1-SNAPSHOT.jar app.jar

# ✅ JAR 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]

