# ─────────────────────────────────────────────
# Etapa 1: build do projeto com Maven
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia wrapper e pom primeiro para aproveitar cache de dependencias
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -q

# Copia o restante e empacota
COPY src src
RUN ./mvnw package -DskipTests -q

# ─────────────────────────────────────────────
# Etapa 2: imagem final enxuta apenas com JRE
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

