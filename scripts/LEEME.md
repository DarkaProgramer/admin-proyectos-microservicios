# Scripts de evidencia (ejecutar en PowerShell, en tu PC)

Orden recomendado (Laragon con MySQL encendido, dump `dbgestor` importado):

1. `scripts\pruebas-bd.ps1`  -> pruebas de base de datos -> `evidencia\bd-resultados.txt`
2. Levantar el backend (JDK 21):  `cd Ejercicio5MyAPIGestor; mvn spring-boot:run`  (puerto 8081)
   Levantar el cliente en otra terminal: `cd Ejercicio5Cliente; mvn spring-boot:run` (puerto 9080)
3. `scripts\pruebas-endpoints.ps1` -> `evidencia\endpoints-resultados.csv/.md`
4. Detener los servicios y correr `scripts\ejecutar-pruebas-maven.ps1` -> logs de `mvn clean test`, JaCoCo y `resumen-cobertura.txt`

Si PowerShell bloquea scripts: `Set-ExecutionPolicy -Scope Process Bypass`
