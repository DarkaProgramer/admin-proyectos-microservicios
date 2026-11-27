# ---------- Etapa 1: compilar con Maven y JDK 21 ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Etapa 2: imagen final ligera con solo el JRE 21 ----------
FROM eclipse-temurin:21-jre
# Se ejecuta con un usuario sin privilegios (no root).
RUN groupadd --system app && useradd --system --gid app --no-create-home app
WORKDIR /app
COPY --from=build --chown=app:app /app/target/*.jar app.jar
USER app
ENV JAVA_OPTS="-Xmx256m"
EXPOSE 9080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
