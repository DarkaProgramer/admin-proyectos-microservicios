# Terraform, GitHub Actions y la arquitectura en la nube explicados

Alumno: Claudio Ángel Huerta Ducoing, GIDS6102.

## 1. Terraform (infraestructura como código)

**Qué es.** Terraform es una herramienta que crea servidores y servicios en la nube a partir de archivos de texto. En lugar de entrar a Render y hacer clic para crear cada servicio, se escribe lo que se necesita y Terraform lo crea. Si algo cambia, cambia el archivo y Terraform aplica solo la diferencia.

**Cómo ayuda.** El despliegue queda repetible (el mismo código crea el mismo entorno), versionado en Git (se sabe quién cambió qué y cuándo) y se puede destruir y volver a crear en minutos. Se evitan los errores de configurar a mano.

**Archivos de la carpeta `terraform/`**

| Archivo | Para qué sirve |
| --- | --- |
| `versions.tf` | Indica qué proveedor se usa: `render-oss/render`, que sabe hablar con la API de Render |
| `variables.tf` | Datos que cambian: región, plan, repositorio, host y usuario de TiDB, contraseñas |
| `main.tf` | Los recursos: los servicios web de Render (backend, frontend, Prometheus y Grafana) |
| `outputs.tf` | Lo que se muestra al terminar: las URL de cada servicio |
| `terraform.tfvars.example` | Ejemplo de valores. El archivo real (`terraform.tfvars`) está en `.gitignore` |

**Comandos, en orden** (con `RENDER_API_KEY` y `TF_VAR_tidb_password` definidas como variables de entorno, nunca en archivos):

| Comando | Qué hace |
| --- | --- |
| `terraform init` | Descarga el proveedor de Render |
| `terraform plan` | **Muestra qué se va a crear, cambiar o destruir, sin hacer nada.** Sirve para revisar antes de aplicar |
| `terraform apply` | Crea los recursos de verdad |
| `terraform output` | Muestra las URL resultantes |
| `terraform destroy` | Borra todo lo que Terraform creó |

**Cómo leer el plan.** Cada recurso aparece con `+` (se creará), `~` (se modificará) o `-` (se destruirá), y al final un resumen como `Plan: 4 to add, 0 to change, 0 to destroy`. Los valores secretos aparecen como `(sensitive value)`.

**Seguridad.** Las contraseñas y la clave de API van en variables de entorno (`TF_VAR_...` y `RENDER_API_KEY`). `terraform.tfvars` y `terraform.tfstate` están en `.gitignore`, así que no llegan al repositorio.

## 2. GitHub Actions, comparado con Jenkins

**Qué es.** GitHub Actions es el servicio de integración y entrega continua (CI/CD) de GitHub. Cada archivo de la carpeta `.github/workflows/` describe un flujo que se ejecuta solo cuando ocurre algo (un push, un pull request, una hora programada o un clic manual) en máquinas que presta GitHub.

**Nota sobre la "CLI de GitHub".** Hay dos cosas distintas. Lo que reemplaza a Jenkins es **GitHub Actions**. La **CLI de GitHub** (`gh`) es una herramienta de línea de comandos para manejar repositorios, pull requests y ejecuciones de Actions desde la terminal (por ejemplo, `gh run list` o `gh workflow run`). Ayuda a operar Actions, pero no es el motor de CI.

| | Jenkins (Unidad II) | GitHub Actions (ahora) |
| --- | --- | --- |
| Dónde corre | En mi computadora, instalado a mano | En servidores de GitHub, sin instalar nada |
| Configuración | Pipeline en `Jenkinsfile`, más Java, Maven, plugins y credenciales a mano | Archivos YAML en `.github/workflows/`, versionados con el código |
| Cuándo se ejecuta | Al lanzarlo yo o con un sondeo | Automático en cada push o pull request, y programado |
| Disponibilidad | Solo si mi computadora está encendida | Siempre |
| Resultados | En mi Jenkins local | En la pestaña Actions del repositorio, ligados al commit |
| Costo | Gratis, pero con mantenimiento | Gratis para repositorios públicos |
| Compatible con Render | No es un servicio que se despliegue en el plan gratuito | Sí: el código vive en GitHub y Render lo despliega desde ahí |

**Flujos del proyecto**

| Flujo | Qué hace |
| --- | --- |
| `ci-backend.yml` y `ci-frontend.yml` | Compilan, corren las pruebas, miden cobertura con JaCoCo y guardan el reporte |
| `sonar.yml` | Analiza el código en SonarQube Cloud y exige el Quality Gate |
| `semgrep.yml` | Escaneo SAST de seguridad |
| `monitoreo.yml` | Revisa cada 30 minutos que los servicios respondan y abre una incidencia si no |
| `k6.yml` | Prueba de carga bajo demanda |

## 3. Los cinco servicios en la nube

| Servicio | Dónde está | Cómo se creó |
| --- | --- | --- |
| Backend (API REST, puerto 8081) | Render | Terraform (`gestor-iac-backend`) |
| Frontend (web, puerto 9080) | Render | Terraform (`gestor-iac-frontend`) |
| Base de datos (`dbgestor`) | TiDB Cloud | Creada en el panel de TiDB; los servicios se conectan con TLS |
| Prometheus (métricas) | Render | Terraform (`gestor-iac-prometheus`) |
| Grafana (tableros) | Render | Terraform (`gestor-iac-grafana`) |

Flujo: el navegador llama al frontend, el frontend al backend y el backend a la base. Prometheus lee `/actuator/prometheus` de backend y frontend cada 30 segundos y Grafana dibuja esas métricas. La base de datos corre en un servicio administrado, que no se crea con el proveedor de Render.

## 4. Pruebas de rendimiento con k6 (tres tipos)

| Tipo | Archivo | Qué busca |
| --- | --- | --- |
| Humo (smoke) | `k6/smoke.js` | Con 1 usuario durante 30 s, confirma que el sistema responde antes de cargarlo |
| Carga (load) | `k6/load.js` | Carga normal: sube a 10 usuarios, se mantiene 60 s y baja |
| Estrés (stress) | `k6/stress.js` | Sube por escalones hasta 40 usuarios para ver dónde se degrada y si se recupera |

Cómo ejecutarlas y verlas: `.\scripts\k6-nube.ps1 smoke` (o `load` o `stress`). k6 manda sus métricas a Prometheus en la nube y se ven en vivo en Grafana, tablero **"Gestor de Proyectos - Pruebas de carga k6"** (usuarios virtuales, peticiones por segundo, latencia p95, errores y verificaciones). Las pruebas se hacen solo contra el entorno propio y con un límite de 40 usuarios.

## 5. Limitaciones

- El plan gratuito de Render duerme los servicios tras 15 minutos sin uso y limita las horas del mes entre todos los servicios del workspace; conviene borrar los servicios manuales viejos si se dejan los seis de Terraform arriba.
- Prometheus y Grafana en la nube quedan públicos (Grafana solo con acceso de lectura anónimo): es aceptable para una práctica sin datos reales, pero no para producción.
- Los actuadores de Spring Boot están expuestos porque Prometheus los necesita; Semgrep lo reporta.
