FROM openjdk:17-jdk-slim

ARG JAR_FILE=build/libs/*.jar
ARG PROFILES
ARG ENV

# 쿠키 파일을 위한 디렉토리 생성
RUN mkdir -p /app/config

# JAR 파일 복사
COPY ${JAR_FILE} app.jar

# 앱 실행
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-Dspring.profiles.active=${PROFILES}", "-Dspring.env=${ENV}", "-jar", "app.jar"]
