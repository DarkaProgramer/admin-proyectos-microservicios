# Terraform - despliegue en Render

Crea los dos Web Services Docker (backend y frontend) a partir del repositorio de GitHub.
La base de datos TiDB Cloud ya existe; Terraform solo recibe sus datos de conexion.

## Secretos (nunca en archivos del repo)
PowerShell:
    $env:RENDER_API_KEY = "rnd_xxxxx"        # Render > Account Settings > API Keys
    $env:TF_VAR_tidb_password = "xxxxx"      # contrasena de TiDB

Copiar `terraform.tfvars.example` a `terraform.tfvars` y llenar `render_owner_id`, `tidb_host`, `tidb_user`.

## Comandos
    terraform init
    terraform validate
    terraform plan
    terraform apply
    terraform output
    terraform destroy        # al terminar la demo, para liberar horas gratis

Si `plan = "free"` es rechazado por el proveedor, usar el Blueprint `render.yaml` de la raiz.
