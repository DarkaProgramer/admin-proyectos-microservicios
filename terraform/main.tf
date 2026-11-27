# Infraestructura como Codigo del Gestor de Proyectos en Render.
# La API key se lee de la variable de entorno RENDER_API_KEY (nunca va en archivos).
provider "render" {
  owner_id = var.render_owner_id
}

# ---------- Backend (Ejercicio5MyAPIGestor) ----------
resource "render_web_service" "backend" {
  environment_id    = var.render_environment_id
  name              = "${var.prefix}-backend"
  plan              = var.render_plan
  region            = var.region
  health_check_path = "/actuator/health"

  runtime_source = {
    docker = {
      repo_url        = var.repo_url
      branch          = var.branch
      dockerfile_path = "./Ejercicio5MyAPIGestor/Dockerfile"
      context         = "./Ejercicio5MyAPIGestor"
      auto_deploy     = true
    }
  }

  env_vars = {
    SPRING_DATASOURCE_URL      = { value = "jdbc:mariadb://${var.tidb_host}:4000/dbgestor?useSsl=true&trustServerCertificate=true" }
    SPRING_DATASOURCE_USERNAME = { value = var.tidb_user }
    SPRING_DATASOURCE_PASSWORD = { value = var.tidb_password }
    JAVA_OPTS                  = { value = var.java_opts }
  }
}

# ---------- Frontend (Ejercicio5Cliente) ----------
resource "render_web_service" "frontend" {
  environment_id = var.render_environment_id
  name           = "${var.prefix}-frontend"
  plan           = var.render_plan
  region         = var.region

  runtime_source = {
    docker = {
      repo_url        = var.repo_url
      branch          = var.branch
      dockerfile_path = "./Ejercicio5Cliente/Dockerfile"
      context         = "./Ejercicio5Cliente"
      auto_deploy     = true
    }
  }

  env_vars = {
    BACKEND_URL = { value = render_web_service.backend.url }
    JAVA_OPTS   = { value = var.java_opts }
  }
}

# ---------- Prometheus (monitoreo en la nube) ----------
resource "render_web_service" "prometheus" {
  environment_id    = var.render_environment_id
  name              = "${var.prefix}-prometheus"
  plan              = var.render_plan
  region            = var.region
  health_check_path = "/-/healthy"

  runtime_source = {
    docker = {
      repo_url        = var.repo_url
      branch          = var.branch
      dockerfile_path = "./monitoring/cloud/prometheus/Dockerfile"
      context         = "./monitoring"
      auto_deploy     = true
    }
  }

  env_vars = {
    BACKEND_HOST  = { value = replace(render_web_service.backend.url, "https://", "") }
    FRONTEND_HOST = { value = replace(render_web_service.frontend.url, "https://", "") }
  }
}

# ---------- Grafana (tableros en la nube) ----------
resource "render_web_service" "grafana" {
  environment_id    = var.render_environment_id
  name              = "${var.prefix}-grafana"
  plan              = var.render_plan
  region            = var.region
  health_check_path = "/api/health"

  runtime_source = {
    docker = {
      repo_url        = var.repo_url
      branch          = var.branch
      dockerfile_path = "./monitoring/cloud/grafana/Dockerfile"
      context         = "./monitoring"
      auto_deploy     = true
    }
  }

  env_vars = {
    PROMETHEUS_URL             = { value = render_web_service.prometheus.url }
    GF_SERVER_HTTP_PORT        = { value = "10000" }
    GF_SECURITY_ADMIN_USER     = { value = "admin" }
    GF_SECURITY_ADMIN_PASSWORD = { value = var.grafana_admin_password }
    GF_AUTH_ANONYMOUS_ENABLED  = { value = "true" }
    GF_AUTH_ANONYMOUS_ORG_ROLE = { value = "Viewer" }
  }
}
