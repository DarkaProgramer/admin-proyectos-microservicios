# Plan de monitoreo y justificación del cambio (Jenkins → GitHub Actions + SonarQube Cloud)

Proyecto: Gestor de Proyectos (microservicios). Alumno: Claudio Ángel Huerta Ducoing, GIDS6102.

## 1. Qué cambia

| Antes (local) | Ahora (nube) | Archivo nuevo |
| --- | --- | --- |
| Jenkins `pipeline-backend` | GitHub Actions: compilar, probar, JaCoCo y empaquetar | `.github/workflows/ci-backend.yml` |
| Jenkins `pipeline-frontend` | GitHub Actions, mismo flujo | `.github/workflows/ci-frontend.yml` |
| Jenkins `pipeline-sonar` + SonarQube local (puerto 9000) | SonarQube Cloud, un solo proyecto con ambos servicios, y Quality Gate que bloquea el flujo | `.github/workflows/sonar.yml` |
| Jenkins `Jenkinsfile.semgrep` (necesitaba Docker local) | Semgrep como contenedor del runner | `.github/workflows/semgrep.yml` |
| Revisión manual de Grafana en la compu | Revisión automática de disponibilidad y latencia cada 30 minutos, con incidencia automática | `.github/workflows/monitoreo.yml` |
| k6 a mano contra localhost | k6 contra la nube, bajo demanda | `.github/workflows/k6.yml` |

Los `Jenkinsfile.*` se conservan en `jenkins/` solo como referencia histórica de la Unidad II.

## 2. Plan de monitoreo

| Qué se vigila | Con qué | Cuándo | Qué pasa si falla |
| --- | --- | --- | --- |
| Que el código compile y las pruebas pasen | `ci-backend.yml` y `ci-frontend.yml` | En cada push y pull request | El flujo queda en rojo y GitHub avisa por correo |
| Calidad, cobertura, duplicación y seguridad del código | SonarQube Cloud (Quality Gate) | En cada push y pull request | El Quality Gate falla y el flujo se detiene |
| Vulnerabilidades en el código fuente | Semgrep | En cada push y pull request | El reporte queda archivado en el flujo |
| Disponibilidad de backend y frontend en Render | `monitoreo.yml` (`/actuator/health` y `/login`) | Cada 30 minutos | El flujo falla, GitHub avisa y se abre una incidencia con la etiqueta `monitoreo` |
| Latencia | Tiempo de respuesta que registra `monitoreo.yml` en cada revisión | Cada 30 minutos | Se ve la tendencia en el resumen de cada ejecución |
| Rendimiento bajo carga | k6 (`k6.yml`) con umbrales p95 < 1000 ms y errores < 1 % | Manual, antes de entregas | El flujo falla si se rompen los umbrales |
| Métricas internas de la JVM | Prometheus y Grafana en `docker-compose.yml` | Cuando se levanta el entorno local | Se usa para diagnóstico y para la demostración |

## 3. Justificación

1. **Los servicios ya viven en la nube.** Jenkins y SonarQube local solo corrían en la computadora del alumno: si se apagaba, no había integración ni análisis. GitHub Actions corre en servidores de GitHub, sin depender de ninguna máquina propia.
2. **Un solo lugar.** El código, los flujos, los resultados y el historial están en el mismo repositorio. Cada resultado queda ligado al commit que lo causó, lo que da trazabilidad.
3. **Sin infraestructura que mantener.** Jenkins requería instalar Java, Maven, plugins, credenciales y rutas de Windows (`C:\Tools\...`). Los flujos de Actions se versionan como código y se reproducen en cualquier ejecución.
4. **Costo.** GitHub Actions y SonarQube Cloud son gratis para repositorios públicos, igual que el plan gratuito de Render y TiDB usado en el proyecto.
5. **El monitoreo en la nube necesita un vigilante externo.** Prometheus y Grafana locales no pueden ver la nube cuando la computadora está apagada. Un flujo programado revisa desde afuera, como lo haría un usuario, y además ayuda a detectar los arranques en frío del plan gratuito de Render.
6. **Calidad como puerta de entrada.** Con `sonar.qualitygate.wait=true`, el código que no cumple el Quality Gate no pasa. En Jenkins esto dependía de que alguien lanzara el pipeline.

Limitaciones: Actions con `schedule` puede retrasarse algunos minutos y el repositorio inactivo por 60 días pausa los flujos programados. Prometheus y Grafana se conservan para ver las métricas de la JVM durante pruebas y demostraciones.

## 4. Configuración que se hace una sola vez

1. En https://sonarcloud.io: importar el repositorio (queda como un solo proyecto) y, en Administration → Analysis Method, **desactivar "Automatic Analysis"** para usar el análisis desde GitHub Actions (así se incluye la cobertura de JaCoCo).
2. En GitHub: Settings → Secrets and variables → Actions.
   - Secret `SONAR_TOKEN`: token generado en SonarQube Cloud (My Account → Security).
   - Variable `SONAR_ORGANIZATION`: clave de la organización.
   - Variable `SONAR_PROJECT_KEY`: clave del proyecto (Administration → Update Key).
   - Variables opcionales `BACK_URL` y `FRONT_URL` si las URL de Render son otras.
3. Hacer push y revisar la pestaña Actions.
