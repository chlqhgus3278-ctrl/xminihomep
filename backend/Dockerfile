# --- 빌드 스테이지 ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 의존성만 먼저 받아 두면 src만 바뀐 재배포에서 이 레이어를 캐시로 재사용한다.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# --- 실행 스테이지 ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Render 무료 인스턴스는 512MB뿐이라 힙을 컨테이너 비율로 묶고,
# 코어가 0.1개라 병렬 GC보다 SerialGC가 메모리/기동 면에서 유리하다.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60.0 -XX:+UseSerialGC -Xss512k -Dspring.jmx.enabled=false"

# 실제 포트는 Render가 주입하는 $PORT를 application.yml에서 읽는다.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
