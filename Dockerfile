# 1단계: Maven으로 빌드 (모든 의존성 포함)
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2단계: 런타임 이미지
FROM eclipse-temurin:21-jdk

WORKDIR /app

# netcat 설치 (대기 로직에 사용된다면)
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# shaded jar 복사 (모든 라이브러리 포함)
COPY --from=builder /app/target/NemReader-1.0-SNAPSHOT.jar app.jar

# 엔트리포인트 스크립트 복사
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]
