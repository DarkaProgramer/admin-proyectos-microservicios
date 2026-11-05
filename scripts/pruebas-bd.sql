-- Pruebas de base de datos (MySQL 8.4 / Laragon). Ejecutar con scripts\pruebas-bd.ps1 (usa --force para registrar errores esperados)
USE dbgestor;
SELECT 'BD-01 conexion' AS caso, DATABASE() AS base, VERSION() AS version, NOW() AS fecha;
SELECT 'BD-02 conteos' AS caso, 'usuario' AS tabla, COUNT(*) AS filas FROM usuario
UNION ALL SELECT 'BD-02 conteos','proyectos',COUNT(*) FROM proyectos
UNION ALL SELECT 'BD-02 conteos','tareas',COUNT(*) FROM tareas
UNION ALL SELECT 'BD-02 conteos','comentarios',COUNT(*) FROM comentarios
UNION ALL SELECT 'BD-02 conteos','asignaciones',COUNT(*) FROM asignaciones;
-- CRUD
INSERT INTO usuario(nombre,correo_electronico,contrasena,rol) VALUES ('ZZ_BD_TEST','zz_bd@test.com','x','Estudiante');
SELECT 'BD-03 INSERT usuario' AS caso, COUNT(*) AS filas_esperadas_1 FROM usuario WHERE nombre='ZZ_BD_TEST';
UPDATE usuario SET rol='Admin' WHERE nombre='ZZ_BD_TEST';
SELECT 'BD-04 UPDATE usuario' AS caso, rol AS rol_esperado_Admin FROM usuario WHERE nombre='ZZ_BD_TEST';
DELETE FROM usuario WHERE nombre='ZZ_BD_TEST';
SELECT 'BD-05 DELETE usuario' AS caso, COUNT(*) AS filas_esperadas_0 FROM usuario WHERE nombre='ZZ_BD_TEST';
-- Restricciones y datos invalidos (se esperan ERRORES 1062 / 1406 / 1292)
SELECT 'BD-06 PK duplicada (esperado: ERROR 1062)' AS caso;
INSERT INTO usuario(id_usuario,nombre) VALUES (131,'duplicado');
SELECT 'BD-07 texto mayor a varchar(100) (esperado: ERROR 1406)' AS caso;
INSERT INTO usuario(nombre) VALUES (REPEAT('A',150));
SELECT 'BD-08 fecha invalida (esperado: ERROR 1292)' AS caso;
INSERT INTO proyectos(nombre,fecha_inicio) VALUES ('ZZ_BD_TEST','2024-13-45');
SELECT 'BD-09 entero invalido (esperado: ERROR 1366)' AS caso;
INSERT INTO tareas(nombre,id_proyecto) VALUES ('ZZ_BD_TEST','abc');
-- Integridad referencial: el dump no define FOREIGN KEY, se espera que NO falle (hallazgo)
INSERT INTO asignaciones(id_usuario,id_tarea,fecha_asignacion) VALUES (99999,99999,'2024-06-26');
SELECT 'BD-10 asignacion con usuario/tarea inexistentes (sin FK: se inserta)' AS caso, COUNT(*) AS filas FROM asignaciones WHERE id_usuario=99999;
DELETE FROM asignaciones WHERE id_usuario=99999;
SELECT 'BD-11 huerfanos asignaciones->usuario' AS caso, COUNT(*) AS huerfanos FROM asignaciones a LEFT JOIN usuario u ON u.id_usuario=a.id_usuario WHERE u.id_usuario IS NULL;
SELECT 'BD-12 huerfanos tareas->proyectos' AS caso, COUNT(*) AS huerfanos FROM tareas t LEFT JOIN proyectos p ON p.id_proyecto=t.id_proyecto WHERE p.id_proyecto IS NULL;
SELECT 'BD-13 huerfanos asignaciones->tareas' AS caso, COUNT(*) AS huerfanos FROM asignaciones a LEFT JOIN tareas t ON t.id_tarea=a.id_tarea WHERE t.id_tarea IS NULL;
SELECT 'BD-14 limpieza (todo en 0)' AS caso,
 (SELECT COUNT(*) FROM usuario WHERE nombre LIKE 'ZZ_%') AS usuarios_zz,
 (SELECT COUNT(*) FROM proyectos WHERE nombre LIKE 'ZZ_%') AS proyectos_zz,
 (SELECT COUNT(*) FROM tareas WHERE nombre LIKE 'ZZ_%') AS tareas_zz;
