# INGENIERÍA EN DESARROLLO Y GESTIÓN DE SOFTWARE
## 2da. UNIDAD — PRÁCTICA 3 (PARTE 1)
**Documento Técnico: Migración de Microservicios a Java 21 LTS, Configuración de JDK 21 / Maven en Jenkins e Integración de Plugins (Surefire, JaCoCo y SonarScanner)**

| Campo | Información Oficial |
| :--- | :--- |
| **Alumno** | Claudio Angel Huerta Ducoing |
| **Grupo** | GIDS102 |
| **Materia** | Gestión del Proceso de Desarrollo de Software |
| **Profesor** | Ramírez Ortiz Enrique |
| **Repositorio GitHub** | https://github.com/DarkaProgramer/admin-proyectos-microservicios.git |
| **Microservicios** | 1) `Ejercicio5MyAPIGestor` (Backend API REST - Puerto `8081`)<br>2) `Ejercicio5Cliente` (Frontend + Cliente Web - Puerto `9080`) |
| **Stack y Versiones** | Java `21.0.13` LTS (Eclipse Temurin), Apache Maven `3.10.0`, Spring Boot `3.2.5` / `2.7.17`, MariaDB, JUnit `5.10.2`, JaCoCo `0.8.11`, Surefire `3.2.5`, SonarScanner `5.5.0.6356` |

---

## ÍNDICE DE CONTENIDO
1. [Resumen Ejecutivo y Estado Inicial de los Microservicios](#1-resumen-ejecutivo-y-estado-inicial-de-los-microservicios)
2. [Instalación y Configuración de JDK 21 (Temurin) y Maven sin afectar JDK 17](#2-instalación-y-configuración-de-jdk-21-temurin-y-maven-sin-afectar-jdk-17)
3. [Detalle Técnico de Cambios en los Archivos `pom.xml` (Qué cambió y Por qué)](#3-detalle-técnico-de-cambios-en-los-archivos-pomxml-qué-cambió-y-por-qué)
4. [Problemas Encontrados Durante la Migración a Java 21 y Soluciones Aplicadas](#4-problemas-encontrados-durante-la-migración-a-java-21-y-soluciones-aplicadas)
5. [Configuración de JDK 21 y Maven Compatible en Jenkins](#5-configuración-de-jdk-21-y-maven-compatible-en-jenkins)
6. [Evidencia Real de Compilación y Ejecución de Pruebas (`mvn clean test`) con Java 21](#6-evidencia-real-de-compilación-y-ejecución-de-pruebas-mvn-clean-test-con-java-21)

---

## 1. Resumen Ejecutivo y Estado Inicial de los Microservicios

El sistema Administrador de Proyectos está compuesto por dos microservicios desarrollados con Spring Boot y una base de datos relacional MariaDB (`dbgestor`). Antes de esta migración, ambos proyectos operaban en versiones distintas de Java (`17` y `11`) y carecían de una configuración homogénea de plugins de pruebas, cobertura y análisis estático.

| Microservicio | Estado Original (Antes) | Estado Actualizado (Después - Java 21) |
| :--- | :--- | :--- |
| **`Ejercicio5Cliente`**<br>(Frontend / BFF - Puerto `9080`) | - Java: `17`<br>- Spring Boot: `3.2.5`<br>- Plugins: `spring-boot`, `surefire 3.2.5`, `jacoco 0.8.11`<br>- Sin `sonar-maven-plugin` ni propiedades Sonar<br>- Sin `argLine` para ByteBuddy en Java 21 | - Java: `21` (`maven.compiler.release = 21`)<br>- Spring Boot: `3.2.5`<br>- Plugins: `spring-boot`, `compiler` (`release 21`), `surefire 3.2.5` (con `argLine`), `jacoco 0.8.11`, `sonar-maven-plugin 5.5.0.6356`<br>- Exclusión `**/target/**` configurada |
| **`Ejercicio5MyAPIGestor`**<br>(Backend API - Puerto `8081`) | - Java: `11`<br>- Spring Boot: `2.7.17`<br>- Plugins: únicamente `spring-boot-maven-plugin`<br>- Sin Surefire, sin JaCoCo y sin SonarScanner<br>- ByteBuddy `1.12.23` (incompatible con Java 21) | - Java: `21` (`maven.compiler.release = 21`)<br>- Spring Boot: `2.7.17` con ByteBuddy `1.14.13` y JUnit Jupiter `5.10.2`<br>- Plugins: `spring-boot`, `compiler 3.11.0` (`release 21`), `surefire 3.2.5`, `jacoco 0.8.11`, `sonar-maven-plugin 5.5.0.6356`<br>- Exclusión `**/target/**` configurada |

### Ubicación de los cuatro archivos POM en el repositorio:
- **POM Original Cliente:** `entrega/poms/original/pom-Ejercicio5Cliente.xml` (y `entrega/poms/original/Ejercicio5Cliente/pom.xml`)
- **POM Original Backend API:** `entrega/poms/original/pom-Ejercicio5MyAPIGestor.xml` (y `entrega/poms/original/Ejercicio5MyAPIGestor/pom.xml`)
- **POM Actualizado Cliente (Java 21):** `entrega/poms/actualizado/pom-Ejercicio5Cliente.xml` (y `Ejercicio5Cliente/pom.xml`)
- **POM Actualizado Backend API (Java 21):** `entrega/poms/actualizado/pom-Ejercicio5MyAPIGestor.xml` (y `Ejercicio5MyAPIGestor/pom.xml`)

---

## 2. Instalación y Configuración de JDK 21 (Temurin) y Maven sin afectar JDK 17

En el entorno de trabajo se conservó intacta la instalación de Oracle JDK `17.0.12` (`C:\Program Files\Java\jdk-17`) y se instaló **Eclipse Temurin JDK 21.0.13+9 LTS** en `C:\Tools\jdk-21.0.13+9`, junto a Apache Maven `3.10.0` (`C:\Tools\apache-maven-3.10.0`).

### Comandos en PowerShell para activar JDK 21 sin romper JDK 17:
```powershell
# Cambiar a JDK 21 en la sesión actual de terminal:
$env:JAVA_HOME = "C:\Tools\jdk-21.0.13+9"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Opcional: Fijar JAVA_HOME a nivel de usuario (sin tocar variables de sistema ni borrar JDK 17):
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Tools\jdk-21.0.13+9", "User")

# Verificar versiones activas:
java -version
mvn -version
```

### Salida real obtenida en consola:
```text
openjdk version "21.0.13" 2026-10-20 LTS
OpenJDK Runtime Environment Temurin-21.0.13+9 (build 21.0.13+9-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.13+9 (build 21.0.13+9-LTS, mixed mode, sharing)

Apache Maven 3.10.0 (c43a36b8d67be7e0805a411bc0898af1a51f5472)
Maven home: C:\Tools\apache-maven-3.10.0
Java version: 21.0.13, vendor: Eclipse Adoptium, runtime: C:\Tools\jdk-21.0.13+9
Default locale: es_MX, platform encoding: UTF-8, time zone: America/Mexico_City
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

> **[CAPTURA: Terminal PowerShell ejecutando `java -version` y `mvn -version` mostrando Eclipse Temurin 21.0.13 y Apache Maven 3.10.0]**

---

## 3. Detalle Técnico de Cambios en los Archivos `pom.xml` (Qué cambió y Por qué)

| Elemento / Sección del POM | Cambio Aplicado | Justificación Técnica (Por qué se cambió) |
| :--- | :--- | :--- |
| `<java.version>`, `<maven.compiler.source>`, `<maven.compiler.target>`, `<maven.compiler.release>` | Actualizado de `17` (en Cliente) y `11` (en APIGestor) a `21`. | Garantiza que `javac` genere bytecode versión `65.0` (Java 21 LTS) y valide la API estándar de Java 21 mediante `--release 21`. |
| `maven-compiler-plugin` (`3.11.0`) | Se configuró explícitamente `<release>21</release>`. | Asegura compatibilidad completa con JDK 21 tanto en compilación local como en pipelines de Jenkins. |
| `<byte-buddy.version>` (en `Ejercicio5MyAPIGestor`) | Actualizado de `1.12.23` a `1.14.13`. | Spring Boot `2.7.17` trae por defecto ByteBuddy `1.12.x`, el cual no soporta class files versión `65` (Java 21). ByteBuddy `1.14.13` soporta plenamente Java 21 para mocks de Mockito. |
| `<junit-jupiter.version>` (en `Ejercicio5MyAPIGestor`) | Actualizado a `5.10.2` y exclusión de `junit-vintage-engine`. | Homologa la plataforma JUnit 5 entre ambos microservicios con `maven-surefire-plugin 3.2.5`. |
| `maven-surefire-plugin` (`3.2.5`) | Agregado en APIGestor y actualizado en ambos con `<argLine>@{argLine} -javaagent:...byte-buddy-agent.jar</argLine>`. | Ejecuta las pruebas JUnit 5 e inyecta al arranque de la JVM tanto el agente de cobertura JaCoCo (`@{argLine}`) como el agente de ByteBuddy, cumpliendo con JEP 451 de Java 21. |
| `jacoco-maven-plugin` (`0.8.11`) | Agregado en APIGestor y configurado en ambos con ejecuciones `prepare-agent` y `report` (fase `test`). | JaCoCo `0.8.11` incluye soporte oficial para instrumentar bytecode de Java 21 y genera `target/site/jacoco/index.html` y `jacoco.xml` automáticamente en `mvn test`. |
| `sonar-maven-plugin` (`5.5.0.6356`) y `<sonar.*>` | Agregado el plugin oficial de SonarScanner y configurado `<sonar.exclusions>**/target/**</sonar.exclusions>`. | Permite ejecutar `mvn sonar:sonar` desde terminal o Jenkins, vinculando el reporte XML de JaCoCo y excluyendo las carpetas compiladas `target`. |

---

## 4. Problemas Encontrados Durante la Migración a Java 21 y Soluciones Aplicadas

| ID | Problema Detectado | Causa Raíz | Solución Implementada |
| :--- | :--- | :--- | :--- |
| **PROB-01** | Bloqueo y advertencia de agente dinámico en Java 21 al usar `Mockito.mockConstruction`:<br>`Corrupted channel... could not open (5) pipe \\.\pipe\javatool-...` | Java 21 implementa **JEP 451**, que restringe la carga dinámica de agentes JVM en tiempo de ejecución (*self-attach* por *named pipes* de Windows) cuando Mockito intenta instrumentar `RestTemplate`. | Se configuró `<argLine>@{argLine} -javaagent:${settings.localRepository}/net/bytebuddy/byte-buddy-agent/${byte-buddy.version}/byte-buddy-agent-${byte-buddy.version}.jar</argLine>` en `maven-surefire-plugin` para cargar el agente desde el arranque de la JVM junto con JaCoCo. |
| **PROB-02** | Advertencia en JaCoCo:<br>`[WARNING] Execution data for class com/mx/cbtis/controller/GestorController does not match` | Existía una copia duplicada de `GestorController.java` dentro de `src/test/java/com/mx/cbtis/controller/` que compilaba en `target/test-classes` y ocultaba la clase real de `src/main/java`. | Se unificó el manejo de excepciones HTTP en `src/main/java/com/mx/cbtis/controller/GestorController.java` y se eliminó el archivo duplicado de `src/test/java`. |
| **PROB-03** | Fallo de pruebas por dependencia de red/BD apagada en `mvn clean test` | `GestorControllerTest` creaba un `RestTemplate` sin mockear las instancias internas del controlador, y `Ejercicio5MyApiGestorApplicationTests` intentaba conectarse a MariaDB (`3306`). | Se actualizó `GestorControllerTest` con `MockedConstruction<RestTemplate>` y `Ejercicio5MyApiGestorApplicationTests` con `@MockBean` para los repositorios, logrando ejecuciones deterministas y autónomas. |

---

## 5. Configuración de JDK 21 y Maven Compatible en Jenkins

1. Ingresar a Jenkins (`http://localhost:8080`) -> **Manage Jenkins** (*Administrar Jenkins*) -> **Tools** (*Herramientas*).
2. **Configuración de JDK 21:** En la sección **JDK installations**, hacer clic en **Add JDK**, desmarcar *Install automatically*, asignar:
   - **Name:** `jdk21` (o `JDK-21`)
   - **JAVA_HOME:** `C:\Tools\jdk-21.0.13+9`
3. **Configuración de Maven:** En la sección **Maven installations**, hacer clic en **Add Maven**, desmarcar *Install automatically*, asignar:
   - **Name:** `maven3` (o `Maven-3.10.0`)
   - **MAVEN_HOME:** `C:\Tools\apache-maven-3.10.0`
4. Hacer clic en **Apply** y **Save**.

> **[CAPTURA: Pantalla de Jenkins -> Manage Jenkins -> Tools mostrando la instalación JDK-21 apuntando a `C:\Tools\jdk-21.0.13+9`]**
>
> **[CAPTURA: Pantalla de Jenkins -> Manage Jenkins -> Tools mostrando la instalación Maven apuntando a `C:\Tools\apache-maven-3.10.0`]**

---

## 6. Evidencia Real de Compilación y Ejecución de Pruebas (`mvn clean test`) con Java 21

### 6.1. Microservicio 1: `Ejercicio5Cliente` (Frontend / Cliente Web)
```text
[INFO] -------------------< com.mx.cbtis:Ejercicio5Cliente >-------------------
[INFO] Building Ejercicio5Cliente 0.0.1-SNAPSHOT
[INFO] --- clean:3.3.2:clean (default-clean) @ Ejercicio5Cliente ---
[INFO] --- jacoco:0.8.11:prepare-agent (prepare-agent) @ Ejercicio5Cliente ---
[INFO] --- compiler:3.11.0:compile (default-compile) @ Ejercicio5Cliente ---
[INFO] Compiling 7 source files with javac [debug release 21] to target\classes
[INFO] --- compiler:3.11.0:testCompile (default-testCompile) @ Ejercicio5Cliente ---
[INFO] Compiling 2 source files with javac [debug release 21] to target\test-classes
[INFO] --- surefire:3.2.5:test (default-test) @ Ejercicio5Cliente ---
[INFO] Running com.mx.cbtis.bromitas.GestorControllerTest
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 12.12 s
[INFO] Running com.mx.cbtis.controller.GestorControllerAutomaticoTest
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.251 s
[INFO] Results:
[INFO] Tests run: 39, Failures: 0, Errors: 0, Skipped: 0
[INFO] --- jacoco:0.8.11:report (report) @ Ejercicio5Cliente ---
[INFO] Loading execution data file ...\Ejercicio5Cliente\target\jacoco.exec
[INFO] Analyzed bundle 'Ejercicio5Cliente' with 7 classes
[INFO] BUILD SUCCESS
```

> **[CAPTURA: Ejecución exitosa de `mvn clean test` en `Ejercicio5Cliente` con Java 21 y generación del reporte JaCoCo]**

### 6.2. Microservicio 2: `Ejercicio5MyAPIGestor` (Backend API REST)
```text
[INFO] -----------------< com.mx.cbtis:Ejercicio5MyAPIGestor >-----------------
[INFO] Building Ejercicio5MyAPIGestor 0.0.1-SNAPSHOT
[INFO] --- clean:3.3.2:clean (default-clean) @ Ejercicio5MyAPIGestor ---
[INFO] --- jacoco:0.8.11:prepare-agent (prepare-agent) @ Ejercicio5MyAPIGestor ---
[INFO] --- compiler:3.11.0:compile (default-compile) @ Ejercicio5MyAPIGestor ---
[INFO] Compiling 16 source files with javac [debug release 21] to target\classes
[INFO] --- compiler:3.11.0:testCompile (default-testCompile) @ Ejercicio5MyAPIGestor ---
[INFO] Compiling 1 source file with javac [debug release 21] to target\test-classes
[INFO] --- surefire:3.2.5:test (default-test) @ Ejercicio5MyAPIGestor ---
[INFO] Running com.mx.cbtis.gestor.Ejercicio5MyApiGestorApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 14.80 s
[INFO] Results:
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] --- jacoco:0.8.11:report (report) @ Ejercicio5MyAPIGestor ---
[INFO] Loading execution data file ...\Ejercicio5MyAPIGestor\target\jacoco.exec
[INFO] Analyzed bundle 'Ejercicio5MyAPIGestor' with 11 classes
[INFO] BUILD SUCCESS
```

> **[CAPTURA: Ejecución exitosa de `mvn clean test` en `Ejercicio5MyAPIGestor` con Java 21 y generación del reporte JaCoCo]**
>
> **[CAPTURA: Repositorio en GitHub mostrando la estructura del proyecto, los commits descriptivos y la carpeta `/entrega/poms/original` y `/entrega/poms/actualizado`]**
