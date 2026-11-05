# Guion del video de ejecucion y aprendizaje (3 a 6 min)

Graba pantalla + voz. Muestra cada punto con evidencia REAL en tu PC.

1. **Presentacion (20 s):** nombre, grupo GIDS102, materia, proyecto (2 microservicios + MySQL).
2. **Repositorio (30 s):** abre el repo en GitHub; muestra `entrega/poms/original` y `actualizado`, `jenkins/` y los commits.
3. **Java 21 (30 s):** terminal con `java -version` y `mvn -version`; muestra en el `pom.xml` `release 21`, Surefire, JaCoCo y Sonar.
4. **Pruebas y cobertura (60 s):** corre `mvn clean test` en cada microservicio; abre `target/site/jacoco/index.html` y muestra 100 % de metodos evaluables y la exclusion justificada.
5. **Jenkins (60 s):** Manage Jenkins > Tools con JDK21 y Maven; ejecuta los 3 pipelines (frontend, backend, analisis) en verde.
6. **SonarQube (45 s):** dashboard con el proyecto, excepcion de `target`, incidencias corregidas (equals(null)).
7. **Aplicacion (30 s):** login en `http://localhost:9080` consumiendo el backend en 8081.
8. **Aprendizaje (45 s):** que aprendiste (migracion a Java 21 y JEP 451 con Mockito, cobertura con asserts reales, CI con Jenkins/Sonar), problemas que resolviste y que mejorarias.
