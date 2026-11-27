variable "render_owner_id" {
  description = "ID del workspace de Render (empieza con tea- o usr-). Dashboard > Workspace Settings."
  type        = string
}

variable "render_plan" {
  description = "Plan de computo de Render. Gratis = free."
  type        = string
  default     = "free"
}

variable "region" {
  description = "Region de Render."
  type        = string
  default     = "oregon"
}

variable "prefix" {
  description = "Prefijo para los nombres de los servicios (evita chocar con los creados a mano)."
  type        = string
  default     = "gestor-iac"
}

variable "repo_url" {
  description = "Repositorio de GitHub con los dos microservicios."
  type        = string
  default     = "https://github.com/DarkaProgramer/admin-proyectos-microservicios"
}

variable "branch" {
  type    = string
  default = "main"
}

variable "tidb_host" {
  description = "Host del cluster TiDB Cloud (gateway....tidbcloud.com)."
  type        = string
}

variable "tidb_user" {
  description = "Usuario de TiDB Cloud."
  type        = string
}

variable "tidb_password" {
  description = "Contrasena de TiDB Cloud. No se guarda en el repositorio."
  type        = string
  sensitive   = true
}

variable "java_opts" {
  type    = string
  default = "-Xmx300m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -Xss512k"
}

variable "grafana_admin_password" {
  description = "Contrasena del usuario admin de Grafana (practica escolar, sin datos reales)."
  type        = string
  sensitive   = true
  default     = "gestor-grafana-2026"
}

variable "render_environment_id" {
  description = "ID del entorno de Render (proyecto Gestor-Admin, Production) donde viven los servicios."
  type        = string
  default     = "evm-dc2sufek1f9s73917cjg"
}
