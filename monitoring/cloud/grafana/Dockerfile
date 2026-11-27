# Grafana para la nube (Render). Contexto de construccion: ./monitoring
FROM grafana/grafana:latest
COPY cloud/grafana/provisioning /etc/grafana/provisioning
COPY grafana/dashboards /var/lib/grafana/dashboards
# Usuario sin privilegios (el usuario "grafana" de la imagen oficial).
USER 472
