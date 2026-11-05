# Sistema Administrador de Proyectos (Arquitectura de Microservicios)

Sistema web de gestión de proyectos, tareas, asignaciones, comentarios y usuarios desarrollado con **Java 21 LTS** y **Spring Boot**, integrado con una base de datos relacional **MySQL 8.4.3 (Laragon)**.

---

## Datos de Identificación
- **Alumno:** Claudio Angel Huerta Ducoing
- **Grupo:** GIDS102
- **Materia:** Gestión del Proceso de Desarrollo de Software (2da. Unidad)
- **Profesor:** Ramírez Ortiz Enrique
- **Repositorio:** `https://github.com/DarkaProgramer/admin-proyectos-microservicios.git`

---

## Arquitectura del Proyecto

El repositorio está compuesto por dos microservicios independientes y el esquema de base de datos:

1. **Base de Datos (`dbgestor` — MySQL 8.4.3 en Laragon, puerto `3306`):**
   - Archivo SQL base: [`dump-dbgestor-202406261453.sql`](./dump-dbgestor-202406261453.sql)
   - Tablas: `usuario`, `proyectos`, `tareas`, `comentarios`, `asignaciones` y `hibernate_sequence`.
2. **Microservicio Backend API REST (`Ejercicio5MyAPIGestor` — Puerto `8081`):**
   - Expone los endpoints RESTful (`/getUsuarioLogin`, `/buscarUsuarios`, `/addUsuario`, `/buscarProyectos`, `/addProyecto`, `/getTarea`, `/addTareas`, `/getComentario`, `/addComentario`, `/buscarAsignaciones`, etc.) consumiendo la base de datos `dbgestor` mediante Spring Data JPA.
3. **Microservicio Frontend / Cliente Web (`Ejercicio5Cliente` — Puerto `9080`):**
   - Sirve la interfaz de usuario con **Thymeleaf**, **Bootstrap 5.3.3** y **jQuery 3.7.1**, comunicándose con el microservicio Backend API en `http://localhost:8081` mediante `RestTemplate`.

---

## Requisitos y Ejecución

### 1. Configurar Java 21 y Maven 3.10.0
```powershell
$env:JAVA_HOME = "C:\Tools\jdk-21.0.13+9"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -version
mvn -version
```

### 2. Importar la Base de Datos en MySQL 8.4.3 (Laragon)
Con el servicio MySQL de Laragon activo en `127.0.0.1:3306` (usuario `root`, sin contraseña):
```powershell
mysql.exe -h 127.0.0.1 -P 3306 -u root -e "CREATE DATABASE IF NOT EXISTS dbgestor;"
mysql.exe -h 127.0.0.1 -P 3306 -u root dbgestor -e "SOURCE dump-dbgestor-202406261453.sql;"
```

### 3. Ejecutar los Microservicios
- **Backend API (`8081`):**
  ```powershell
  cd Ejercicio5MyAPIGestor
  mvn spring-boot:run
  ```
- **Cliente Web (`9080`):**
  ```powershell
  cd Ejercicio5Cliente
  mvn spring-boot:run
  ```
  Abrir en el navegador: `http://localhost:9080`

### 4. Ejecutar Pruebas Automáticas y Reporte JaCoCo
```powershell
# En Ejercicio5MyAPIGestor o Ejercicio5Cliente:
mvn clean test
# El reporte HTML de cobertura se genera en: target/site/jacoco/index.html
```

---

## Estructura de Entregables (`/entrega`)

- [`entrega/poms/original/`](./entrega/poms/original): Respaldo intacto de los 2 archivos `pom.xml` originales antes de la migración.
- [`entrega/poms/actualizado/`](./entrega/poms/actualizado): Los 2 archivos `pom.xml` migrados a **Java 21** con los plugins `maven-surefire-plugin`, `jacoco-maven-plugin` y `sonar-maven-plugin`.
- [`entrega/`](./entrega): Documentos técnicos en formato Word (`.docx`) y Markdown (`.md`) correspondientes a las prácticas de evaluación.

---

## Nota Técnica sobre Recursos Multimedia Excluidos en `.gitignore`

Para evitar que las subidas (`git push`) excedan el tiempo de espera de GitHub (`RPC failed; HTTP 408 Request Timeout`) debido al peso acumulado de ~397 MB en archivos de video y animaciones sin comprimir, se excluyeron en [`.gitignore`](./.gitignore) únicamente los **8 recursos estáticos mayores a 10 MB** ubicados en `Ejercicio5Cliente/src/main/resources/static/Images/`:

| Archivo Excluido en `.gitignore` | Tamaño | Tipo de Recurso |
| :--- | :---: | :--- |
| `Ejercicio5Cliente/src/main/resources/static/Images/manual.mp4` | 81.78 MB | Video del manual de usuario |
| `Ejercicio5Cliente/src/main/resources/static/Images/fondoLogin.gif` | 76.70 MB | Animación de fondo de login |
| `Ejercicio5Cliente/src/main/resources/static/Images/fondo.gif` | 70.96 MB | Animación de fondo general |
| `Ejercicio5Cliente/src/main/resources/static/Images/hola.gif` | 57.93 MB | Animación de bienvenida |
| `Ejercicio5Cliente/src/main/resources/static/Images/tutorial.mp4` | 50.20 MB | Video del tutorial |
| `Ejercicio5Cliente/src/main/resources/static/Images/animacion9.gif` | 28.18 MB | Fondo animado de tarjeta de proyecto |
| `Ejercicio5Cliente/src/main/resources/static/Images/fondo.png` | 20.43 MB | Imagen de fondo en alta resolución |
| `Ejercicio5Cliente/src/main/resources/static/Images/animacion10.gif` | 12.69 MB | Fondo animado de tarjeta de proyecto |

Todos los demás recursos estáticos (58 imágenes `.png`/`.gif`/`.jpg`/`.ico`, hojas de estilo CSS, Bootstrap y scripts JS) están incluidos en el repositorio.
