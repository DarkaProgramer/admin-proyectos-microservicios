# Prometheus para la nube (Render). Contexto de construccion: ./monitoring
FROM prom/prometheus:latest
COPY cloud/prometheus/prometheus.yml.tpl /etc/prometheus/prometheus.yml.tpl
COPY alerts.yml /etc/prometheus/alerts.yml
# Usuario sin privilegios (el usuario "nobody" de la imagen oficial).
USER nobody
# Sustituye los hosts de los servicios, arranca en el puerto PORT de Render y
# habilita la recepcion de metricas de k6 (remote write).
ENTRYPOINT ["/bin/sh", "-c", "sed -e \"s#__BACKEND_HOST__#$BACKEND_HOST#g\" -e \"s#__FRONTEND_HOST__#$FRONTEND_HOST#g\" /etc/prometheus/prometheus.yml.tpl > /tmp/prometheus.yml && exec /bin/prometheus --config.file=/tmp/prometheus.yml --storage.tsdb.path=/prometheus --storage.tsdb.retention.time=3d --web.enable-remote-write-receiver --web.listen-address=0.0.0.0:${PORT:-10000}"]
