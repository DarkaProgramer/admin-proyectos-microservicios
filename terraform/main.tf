# Infraestructura como Codigo del Gestor de Proyectos en Render.
# La API key se lee de la variable de entorno RENDER_API_KEY (nunca va en archivos).
provider "render" {
  owner_id = var.render_owner_id
}

# ---------- Backend (Ejercicio5MyAPIGestor) ----------
resource "render_web_service" "backend" {
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
  name   = "${var.prefix}-frontend"
  plan   = var.render_plan
  region = var.region

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
