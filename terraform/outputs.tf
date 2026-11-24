output "backend_url" {
  description = "URL publica del backend"
  value       = render_web_service.backend.url
}

output "frontend_url" {
  description = "URL publica del frontend"
  value       = render_web_service.frontend.url
}
