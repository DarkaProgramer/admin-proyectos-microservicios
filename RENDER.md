# Despliegue en Render

Arquitectura: **TiDB Cloud** (base de datos MySQL compatible, plan gratuito) + **2 Web Services Docker en Render** (backend y frontend).

## 1) Base de datos (TiDB Cloud)
1. Crear cluster gratuito (Serverless/Starter) en https://tidbcloud.com.
2. Connect -> generar contraseña; anotar host, puerto 4000, usuario y contraseña.
3. Crear la base `dbgestor` e importar `docker/db/init/01-dbgestor.sql` (HeidiSQL con SSL activado).

## 2) Backend (Ejercicio5MyAPIGestor) - Web Service Docker
- Root Directory: `Ejercicio5MyAPIGestor`  |  Runtime: Docker  |  Plan: Free
- Variables de entorno:
  - `SPRING_DATASOURCE_URL` = `jdbc:mariadb://HOST:4000/dbgestor?useSsl=true&trustServerCertificate=true`
  - `SPRING_DATASOURCE_USERNAME` = usuario de TiDB
  - `SPRING_DATASOURCE_PASSWORD` = contraseña de TiDB
  - `JAVA_OPTS` = `-Xmx300m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -Xss512k`

## 3) Frontend (Ejercicio5Cliente) - Web Service Docker
- Root Directory: `Ejercicio5Cliente`  |  Runtime: Docker  |  Plan: Free
- Variables de entorno:
  - `BACKEND_URL` = URL publica del backend (https://....onrender.com, sin diagonal final)
  - `JAVA_OPTS` = `-Xmx300m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -Xss512k`

Los servicios leen el puerto de la variable `PORT` que asigna Render (`server.port=${PORT:...}`).
Nota: en el plan gratuito los servicios se duermen tras 15 min sin trafico; la primera peticion tarda ~1 minuto.
