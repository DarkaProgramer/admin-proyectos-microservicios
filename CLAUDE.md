# CLAUDE.md — Gestor de Proyectos (microservicios)

Contexto para cualquier asistente que trabaje en este repositorio.

## Quién es el usuario
- Alumno: Claudio Ángel Huerta Ducoing, grupo GIDS6102.
- Materia: Gestión del Proceso de Desarrollo de Software. Profesor: Enrique Ramírez Ortíz.
- Habla informal en español y no es muy técnico: dar comandos para copiar y pegar, pasos numerados y cortos.
- Trabaja en Windows (VS Code, PowerShell). Ruta local: `C:\Users\huerd\OneDrive\Desktop\admin-proyectos-main`.
- Repo: https://github.com/DarkaProgramer/admin-proyectos-microservicios (rama `main`).
- Es una práctica escolar: no hay datos reales, y las contraseñas de prueba pueden aparecer en el chat.

## Qué es la aplicación
Gestor de proyectos y tareas tipo tablero (estilo Trello) con dos microservicios:

| Servicio | Carpeta | Tecnología | Puerto local |
|---|---|---|---|
| Backend (API REST + JPA) | `Ejercicio5MyAPIGestor` | Spring Boot 2.7.17, Java 21, MariaDB | 8081 |
| Frontend (web con Thymeleaf) | `Ejercicio5Cliente` | Spring Boot 3.2.5, Java 21, RestTemplate, jQuery/Bootstrap | 9080 |

Funciones: login y registro de usuarios; crear, ver y borrar proyectos; tareas por proyecto en tres listas (`pendientes`, `encurso`, `terminado`) con color de estado (`danger`, `warning`, `primary`, `success`); asignar tareas a usuarios; comentarios por tarea.

Flujo: navegador → frontend (9080) → backend (8081) → base `dbgestor`.
La base tiene 6 tablas: `usuario`, `proyectos`, `tareas`, `asignaciones`, `comentarios`, `hibernate_sequence`.

Detalles útiles del código:
- El backend no tiene capa de servicio; los controladores usan repositorios y DTOs (`gestor/dto`).
- El frontend llama al backend con `backendUrl` (propiedad `backend.url`, variable `BACKEND_URL`).
- Los puertos leen la variable `PORT` (Render la define). Las credenciales de BD se sobrescriben con `SPRING_DATASOURCE_URL/USERNAME/PASSWORD`.
- Parámetros de la API: `/buscarProyecto?id_proyecto=`, `/buscarUsuario?id=`.
- Al reenviar respuestas del backend, el frontend devuelve solo el cuerpo (no las cabeceras). Reenviar el `ResponseEntity` completo causa 502 en Render.

## Hasta dónde se ha llegado
Hecho:
- Práctica 3 Parte 1 (migración a Java 21) y Parte 2 (cobertura con JaCoCo, SonarQube y Jenkins): pipelines en verde, 0 issues en Sonar, 100% de cobertura de métodos. Documentos Word con portada de plantilla en `entrega/`.
- Tests: 112 en backend y 44 en frontend.
- Docker local: Dockerfiles multi-stage en cada servicio y `docker-compose.yml` en la raíz (db MariaDB 11.3 con dump en `docker/db/init/`, backend, frontend). Guía en `DOCKER.md`. Se levanta con `docker compose up -d --build`.
- Despliegue en la nube (plan gratis): base de datos TiDB Cloud Starter (cluster `gestor`, base `dbgestor`, puerto 4000, TLS) y dos Web Services Docker en Render.
  - Backend: https://gestor-backend-19gg.onrender.com (funciona)
  - Frontend: https://gestor-frontend-iocd.onrender.com (se corrigió un 502 en el login; falta confirmar que ya funciona)
  - Variables de Render en `RENDER.md`. Dockerfile Path `./<servicio>/Dockerfile` y Build Context `./<servicio>`.
  - Los servicios gratis se duermen tras 15 min sin uso y tardan hasta ~1 minuto en despertar.
- Datos de prueba en TiDB: 5 usuarios, 3 proyectos, 8 tareas. Login de prueba: `claudio22540@cbtis75.edu.mx` / `claudio26`.

## Pendiente (objetivo: recuperación R1 Unidad 2, mínimo 7.6)
1. Confirmar que el login y crear una tarea funcionan en el sitio de Render.
2. Capturas de evidencia: sitio en Render, los dos servicios en Live, cluster activo en TiDB, `docker compose ps` local.
3. Documento "El despliegue de la aplicación con las imágenes Docker de los micro servicios".
4. Documentación no entregada: Prácticas 1 y 2 (tienen marcadores `[CAPTURA]` pendientes y portada vieja). Usar la portada de plantilla.
5. Opcional: video de la práctica y actualizar `entrega/guion-video.md` (aún dice GIDS102; el grupo correcto es GIDS6102).

## Reglas de trabajo
- No subir a GitHub: `tidb-dbgestor.sql`, `tidb-dbgestor-limpia.sql`, `Entrega_R1_Unidad2_ClaudioHuerta.zip`, `doc*_texto.txt`, `temp_doc*_img/`.
- Los documentos Word van en `entrega/` y se entregan al profesor junto con el enlace de GitHub.
- Antes de cambiar código, correr `mvn -q clean test` en el servicio afectado.
- Para TiDB: no usar `LOCK TABLES`, `/*!...*/` ni collations de MariaDB; usar el script limpio.
