# Ejecuta una prueba k6 contra la nube y envia las metricas a Prometheus (se ven en Grafana).
# Uso:  .\scripts\k6-nube.ps1 smoke   |   .\scripts\k6-nube.ps1 load   |   .\scripts\k6-nube.ps1 stress
# Requiere Docker Desktop.
param([Parameter(Mandatory=$true)][ValidateSet('smoke','load','stress')][string]$Tipo)

$prom = if ($env:PROMETHEUS_URL) { $env:PROMETHEUS_URL } else { 'https://gestor-iac-prometheus.onrender.com' }
Write-Host "Despertando servicios..."
foreach ($u in @('https://gestor-iac-frontend.onrender.com/login','https://gestor-iac-backend.onrender.com/actuator/health',"$prom/-/healthy")) {
  try { Invoke-WebRequest -Uri $u -UseBasicParsing -TimeoutSec 90 | Out-Null } catch { Write-Host "  (aun despertando) $u" }
}
$salida = Join-Path $PSScriptRoot "..\evidencia-u3"
docker run --rm -i `
  -v "${PWD}\k6:/scripts:ro" -v "${salida}:/results" `
  -e K6_PROMETHEUS_RW_SERVER_URL="$prom/api/v1/write" `
  -e K6_PROMETHEUS_RW_TREND_STATS="p(95),p(99),avg,max" `
  grafana/k6 run -o experimental-prometheus-rw --tag testid=$Tipo `
  --summary-export="/results/k6-$Tipo.json" "/scripts/$Tipo.js"
