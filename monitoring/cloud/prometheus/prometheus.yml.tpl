global:
  scrape_interval: 30s
  scrape_timeout: 25s
  evaluation_interval: 30s

rule_files:
  - /etc/prometheus/alerts.yml

scrape_configs:
  - job_name: gestor-backend
    scheme: https
    metrics_path: /actuator/prometheus
    static_configs:
      - targets: ['__BACKEND_HOST__:443']
  - job_name: gestor-frontend
    scheme: https
    metrics_path: /actuator/prometheus
    static_configs:
      - targets: ['__FRONTEND_HOST__:443']
