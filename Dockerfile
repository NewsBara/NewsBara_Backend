FROM openjdk:17-jdk-slim

ARG JAR_FILE=build/libs/*.jar
ARG PROFILES
ARG ENV

# Python과 yt-dlp 설치
RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    pip3 install yt-dlp && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 쿠키 파일을 위한 디렉토리 생성
RUN mkdir -p /app/config

# JAR 파일 복사
COPY ${JAR_FILE} app.jar

# 앱 실행
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-Dspring.profiles.active=${PROFILES}", "-Dspring.env=${ENV}", "-jar", "app.jar"]
