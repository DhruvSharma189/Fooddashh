# ---------- Stage 1: build the React frontend ----------
FROM node:20-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
# vite.config.js writes to ../src/main/resources/static  ->  /app/src/main/resources/static
RUN npm run build

# ---------- Stage 2: build the Spring Boot jar ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
# overwrite static/ with the freshly built frontend
COPY --from=frontend /app/src/main/resources/static ./src/main/resources/static
RUN mvn -q clean package -DskipTests

# ---------- Stage 3: runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/fooddash-1.0.0.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 10010
# Render injects $PORT; fall back to 10010 for local runs
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-10010} -jar app.jar"]
