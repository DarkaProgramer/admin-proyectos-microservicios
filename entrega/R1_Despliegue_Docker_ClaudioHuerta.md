# INGENIERÍA EN DESARROLLO Y GESTIÓN DE SOFTWARE
## EVALUACIÓN DE RECUPERACIÓN R1 — UNIDAD 2
**Documento Técnico: Despliegue de Microservicios con Docker Compose, Imágenes Multi-Stage Java 21 y Compilación de Evidencias de la Unidad 2**

| Campo | Información Oficial |
| :--- | :--- |
| **Alumno** | Claudio Angel Huerta Ducoing |
| **Matrícula** | 1222100432 |
| **Grupo** | GIDS6102 |
| **Materia** | Gestión del Proceso de Desarrollo de Software |
| **Profesor** | Ramírez Ortiz Enrique |
| **Fecha de Presentación** | 23 de noviembre de 2026 |
| **Repositorio GitHub** | [https://github.com/DarkaProgramer/admin-proyectos-microservicios.git](https://github.com/DarkaProgramer/admin-proyectos-microservicios.git) |

---

## 1. Propósito de la Entrega R1 (Unidad 2)

De acuerdo con las indicaciones oficiales de la evaluación de recuperación (R1 - Unidad 2):
1. **Despliegue integral de la aplicación utilizando contenedores Docker** para los microservicios (`Ejercicio5Cliente` y `Ejercicio5MyAPIGestor`) y el motor de base de datos (`MariaDB`), asegurando su interconexión automática y persistencia.
2. **Consolidación y entrega de toda la documentación técnica solicitada en la Unidad 2**:
   - **Práctica 1:** Pruebas de base de datos, diseño técnico de endpoints, corrección de fallas y reprueba.
   - **Práctica 2:** Pruebas de integración frontend-backend y pruebas automáticas con JUnit 5, Mockito y MockMvc (casos de éxito y de fracaso).
   - **Práctica 3 (Parte 1):** Migración a Java 21 LTS, configuración de JDK 21 y Maven en Jenkins, inclusión de plugins (`sonar-maven-plugin 5.5.0.6356`, `jacoco-maven-plugin 0.8.11`, `maven-surefire-plugin 3.2.5`) y evidencia de los 4 archivos POM (2 originales y 2 actualizados).
   - **Práctica 3 (Parte 2):** Cobertura del 100% de métodos evaluables con JaCoCo, análisis de calidad y seguridad con SonarQube (excluyendo carpetas `target`), atención de incidencias/vulnerabilidades y 3 pipelines funcionales en Jenkins (`Jenkinsfile.backend`, `Jenkinsfile.frontend`, `Jenkinsfile.sonar`).

---

## 2. Arquitectura de Despliegue con Docker

El sistema se compone de tres servicios orquestados mediante **Docker Compose**:

```
+-----------------------------------------------------------------------------------+
|                                  RED DOCKER (BRIDGE)                              |
|                                                                                   |
|  [ Navegador Web ]                                                                |
|         |                                                                         |
|         v (Puerto 9080)                                                           |
|  +-------------------------------------+                                          |
|  |       CONTENEDOR FRONTEND           |                                          |
|  |         gestor-frontend:1.0         |                                          |
|  |     (Spring Boot 3.2 / Java 21)     |                                          |
|  +-------------------------------------+                                          |
|         |                                                                         |
|         | BACKEND_URL=http://backend:8081                                         |
|         v (Puerto 8081 interno)                                                   |
|  +-------------------------------------+                                          |
|  |       CONTENEDOR BACKEND            |                                          |
|  |         gestor-backend:1.0          |                                          |
|  |     (Spring Boot 2.7 / Java 21)     |                                          |
|  +-------------------------------------+                                          |
|         |                                                                         |
|         | SPRING_DATASOURCE_URL=jdbc:mariadb://db:3306/dbgestor                   |
|         v (Puerto 3306 interno)                                                   |
|  +-------------------------------------+      +--------------------------------+  |
|  |       CONTENEDOR BASE DE DATOS      | ---> |      VOLUMEN PERSISTENTE       |  |
|  |           mariadb:11.3              |      |            db-data             |  |
|  |    (Inicialización con dump SQL)    |      +--------------------------------+  |
|  +-------------------------------------+                                          |
+-----------------------------------------------------------------------------------+
```

### Tabla de Servicios y Puertos
| Servicio | Imagen | Puerto Host / Interno | Función |
| :--- | :--- | :--- | :--- |
| **Frontend** | `gestor-frontend:1.0` | `9080:9080` | Interfaz de usuario web (Thymeleaf, HTML5, Bootstrap 5). Comunica con el backend vía `BACKEND_URL`. |
| **Backend** | `gestor-backend:1.0` | `8081:8081` | API REST de gestión de proyectos, tareas y usuarios (Spring Data JPA). |
| **Base de Datos** | `mariadb:11.3` | `3306` (interno) | Base de datos relacional `dbgestor`. Inicializada automáticamente con `01-dbgestor.sql`. |

---

## 3. Especificación de los Archivos Dockerfile (Multi-Stage Build)

Para garantizar imágenes ligeras y seguras sin compilar dependencias en el runtime, ambos microservicios utilizan la estrategia **Multi-Stage Build**:
1. **Etapa 1 (Build):** Utiliza `maven:3.9-eclipse-temurin-21` para compilar el código fuente y empaquetar el `.jar` ejecutable con JDK 21.
2. **Etapa 2 (Runtime):** Utiliza `eclipse-temurin:21-jre` (solo el runtime ligero, reduciendo drásticamente el peso de la imagen y la superficie de ataque).

### Dockerfile del Backend (`Ejercicio5MyAPIGestor/Dockerfile`)
```dockerfile
# ---------- Etapa 1: compilar con Maven y JDK 21 ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Etapa 2: imagen final ligera con solo el JRE 21 ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xmx256m"
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Dockerfile del Frontend (`Ejercicio5Cliente/Dockerfile`)
```dockerfile
# ---------- Etapa 1: compilar con Maven y JDK 21 ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Etapa 2: imagen final ligera con solo el JRE 21 ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xmx256m"
EXPOSE 9080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

---

## 4. Archivo Orquestador `docker-compose.yml`

El archivo `docker-compose.yml` en la raíz del proyecto coordina el orden de encendido mediante dependencias (`depends_on`) con validación de estado de salud (`healthcheck` en MariaDB), garantizando que el backend no intente conectarse hasta que la base de datos esté 100% lista para recibir conexiones.

```yaml
services:
  db:
    image: mariadb:11.3
    container_name: gestor-db
    environment:
      MARIADB_ROOT_PASSWORD: root123
      MARIADB_DATABASE: dbgestor
    volumes:
      - ./docker/db/init:/docker-entrypoint-initdb.d:ro   # Carga automática del dump inicial
      - db-data:/var/lib/mysql                           # Persistencia de datos
    healthcheck:
      test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
      interval: 10s
      timeout: 5s
      retries: 12
      start_period: 20s

  backend:
    build: ./Ejercicio5MyAPIGestor
    image: gestor-backend:1.0
    container_name: gestor-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://db:3306/dbgestor
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root123
      JAVA_OPTS: "-Xmx256m"
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "8081:8081"

  frontend:
    build: ./Ejercicio5Cliente
    image: gestor-frontend:1.0
    container_name: gestor-frontend
    environment:
      BACKEND_URL: http://backend:8081
      JAVA_OPTS: "-Xmx256m"
    depends_on:
      - backend
    ports:
      - "9080:9080"

volumes:
  db-data:
```

---

## 5. Instrucciones de Ejecución y Pruebas del Despliegue

### 5.1. Levantar el Entorno Completo
Abrir terminal en la raíz del proyecto (`admin-proyectos-main`):
```bash
# Construir imágenes y arrancar los 3 contenedores
docker-compose up --build -d
```
*(O alternativamente: `docker compose up --build -d`)*

### 5.2. Verificar el Estado de los Contenedores
```bash
docker-compose ps
```
Salida esperada:
```
NAME              IMAGE                 COMMAND                  SERVICE    STATUS
gestor-db         mariadb:11.3          "docker-entrypoint.s…"   db         Up (healthy)
gestor-backend    gestor-backend:1.0    "sh -c 'java $JAVA_O…"   backend    Up
gestor-frontend   gestor-frontend:1.0   "sh -c 'java $JAVA_O…"   frontend   Up
```

### 5.3. Acceso y Comprobación en Navegador
- **Frontend (Aplicación Web):** [http://localhost:9080](http://localhost:9080)
- **Backend (API REST):** [http://localhost:8081/buscarProyectos](http://localhost:8081/buscarProyectos) (petición POST)
- **Credenciales iniciales de prueba:**
  - Correo: `claudio@gmail.com`
  - Contraseña: `123`

### 5.4. Detener el Despliegue
```bash
# Detener conservando los datos de la base de datos
docker-compose down

# Detener y reiniciar limpiando datos de la base de datos
docker-compose down -v
```

---

## 6. Inventario de Documentación Entregada (Paquete Completo Unidad 2)

Todos los entregables oficiales se encuentran organizados dentro de la carpeta `/entrega` del repositorio:

| Archivo / Entregable | Contenido y Alcance Técnico |
| :--- | :--- |
| `entrega/R1_Despliegue_Docker_ClaudioHuerta.md` | Documento técnico de despliegue con Docker Compose, imágenes Java 21 e instrucciones de ejecución. |
| `entrega/Practica1_BD_Endpoints_ClaudioHuerta.docx` | Práctica 1: Pruebas de base de datos MariaDB, endpoints REST, correcciones técnicas y reprueba. |
| `entrega/Practica2_Integracion_Pruebas_ClaudioHuerta.docx` | Práctica 2: Pruebas de integración frontend-backend y pruebas automáticas con casos de éxito y fracaso. |
| `entrega/Practica3_Parte1_Migracion_Java21_ClaudioHuerta.docx` | Práctica 3 (Parte 1): Documento técnico de migración a Java 21, configuración de Jenkins (JDK 21 y Maven), plugins Sonar, JaCoCo y Surefire. |
| `entrega/Practica3_Parte2_Cobertura_Sonar_Jenkins_ClaudioHuerta.docx` | Práctica 3 (Parte 2): Cobertura de métodos al 100%, escaneo de vulnerabilidades SonarQube sin `target/`, y 3 pipelines funcionales en Jenkins. |
| `entrega/poms/original/` | Los 2 archivos `pom.xml` originales antes de la migración (identificados claramente). |
| `entrega/poms/actualizado/` | Los 2 archivos `pom.xml` actualizados a Java 21 con los plugins obligatorios configurados. |
| `entrega/guion-video.md` | Guión para la demostración en video de pipelines, pruebas y resultados. |
| `docker-compose.yml` y `DOCKER.md` | Archivo orquestador y guía de ejecución de contenedores Docker. |
