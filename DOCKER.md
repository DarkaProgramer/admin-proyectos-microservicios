# Despliegue con Docker

Tres contenedores: **MariaDB** (`dbgestor`), **backend** (`Ejercicio5MyAPIGestor`, puerto 8081) y **frontend** (`Ejercicio5Cliente`, puerto 9080).

## Requisitos
Docker Desktop (o Docker Engine con Compose v2).

## Levantar todo
```
docker compose up --build
```
La primera vez construye las dos imagenes (Maven + JDK 21) y carga el dump `docker/db/init/01-dbgestor.sql`.

- Aplicacion: http://localhost:9080
- API backend: http://localhost:8081/buscarProyectos (POST)

## Apagar
```
docker compose down        # conserva la base de datos
docker compose down -v     # borra tambien los datos
```

## Imagenes
| Imagen | Origen | Puerto |
|---|---|---|
| gestor-backend:1.0 | Ejercicio5MyAPIGestor/Dockerfile | 8081 |
| gestor-frontend:1.0 | Ejercicio5Cliente/Dockerfile | 9080 |
| mariadb:11.3 | imagen oficial | 3306 (interno) |

## Variables de configuracion
- Backend: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.
- Frontend: `BACKEND_URL` (por defecto `http://localhost:8081` fuera de Docker).
