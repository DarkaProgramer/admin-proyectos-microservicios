output "backend_url" {
  description = "URL publica del backend"
  value       = render_web_service.backend.url
}

output "frontend_url" {
  description = "URL publica del frontend"
  value       = render_web_service.frontend.url
}

output "prometheus_url" {
  description = "URL publica de Prometheus"
  value       = render_web_service.prometheus.url
}

output "grafana_url" {
  description = "URL publica de Grafana"
  value       = render_web_service.grafana.url
}
