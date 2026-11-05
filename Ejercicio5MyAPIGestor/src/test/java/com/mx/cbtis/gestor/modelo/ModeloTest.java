package com.mx.cbtis.gestor.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Pruebas de las entidades JPA: constructores, getters y setters. */
class ModeloTest {

	@Test
	void usuario_constructorCompletoYSetters() {
		Usuario u = new Usuario(1, "Ana", "ana@test.com", "1234", "admin", "img.png");
		assertEquals(Integer.valueOf(1), u.getId_usuario());
		assertEquals("Ana", u.getNombre());
		assertEquals("ana@test.com", u.getCorreo_electronico());
		assertEquals("1234", u.getContrasena());
		assertEquals("admin", u.getRol());
		assertEquals("img.png", u.getImagen_usuario());
		Usuario v = new Usuario();
		assertNull(v.getId_usuario());
		v.setId_usuario(2); v.setNombre("Luis"); v.setCorreo_electronico("l@t.com");
		v.setContrasena("abcd"); v.setRol("user"); v.setImagen_usuario("x.png");
		assertEquals(Integer.valueOf(2), v.getId_usuario());
		assertEquals("Luis", v.getNombre());
		assertEquals("l@t.com", v.getCorreo_electronico());
		assertEquals("abcd", v.getContrasena());
		assertEquals("user", v.getRol());
		assertEquals("x.png", v.getImagen_usuario());
	}

	@Test
	void proyectos_constructorCompletoYSetters() {
		Proyectos p = new Proyectos(1, "P", "d", "2024-01-01", "2024-02-01", "activo", "f.png");
		assertEquals(Integer.valueOf(1), p.getId_proyecto());
		assertEquals("P", p.getNombre());
		assertEquals("d", p.getDescripcion());
		assertEquals("2024-01-01", p.getFecha_inicio());
		assertEquals("2024-02-01", p.getFecha_finalizacion());
		assertEquals("activo", p.getEstado_proyecto());
		assertEquals("f.png", p.getFondo());
		Proyectos q = new Proyectos();
		assertNull(q.getId_proyecto());
		q.setId_proyecto(2); q.setNombre("Q"); q.setDescripcion("e"); q.setFecha_inicio("a");
		q.setFecha_finalizacion("b"); q.setEstado_proyecto("fin"); q.setFondo("g.png");
		assertEquals(Integer.valueOf(2), q.getId_proyecto());
		assertEquals("Q", q.getNombre());
		assertEquals("e", q.getDescripcion());
		assertEquals("a", q.getFecha_inicio());
		assertEquals("b", q.getFecha_finalizacion());
		assertEquals("fin", q.getEstado_proyecto());
		assertEquals("g.png", q.getFondo());
	}

	@Test
	void tareas_constructorCompletoYSetters() {
		Tareas t = new Tareas(1, "T", "d", "2024-03-01", "lista1", "pendiente", 5);
		assertEquals(Integer.valueOf(1), t.getId_tarea());
		assertEquals("T", t.getNombre());
		assertEquals("d", t.getDescripcion());
		assertEquals("2024-03-01", t.getFecha_vencimiento());
		assertEquals("lista1", t.getLista());
		assertEquals("pendiente", t.getEstado());
		assertEquals(Integer.valueOf(5), t.getId_proyecto());
		Tareas u = new Tareas();
		assertNull(u.getId_tarea());
		u.setId_tarea(2); u.setNombre("U"); u.setDescripcion("e"); u.setFecha_vencimiento("f");
		u.setLista("l2"); u.setEstado("hecho"); u.setId_proyecto(6);
		assertEquals(Integer.valueOf(2), u.getId_tarea());
		assertEquals("U", u.getNombre());
		assertEquals("e", u.getDescripcion());
		assertEquals("f", u.getFecha_vencimiento());
		assertEquals("l2", u.getLista());
		assertEquals("hecho", u.getEstado());
		assertEquals(Integer.valueOf(6), u.getId_proyecto());
	}

	@Test
	void comentarios_constructorCompletoYSetters() {
		Comentarios c = new Comentarios(1, "Ana", 2, "hola", "2024-06-26 10:00");
		assertEquals(Integer.valueOf(1), c.getId_comentario());
		assertEquals("Ana", c.getId_usuario());
		assertEquals(Integer.valueOf(2), c.getId_tarea());
		assertEquals("hola", c.getTexto_comentario());
		assertEquals("2024-06-26 10:00", c.getFecha_hora());
		Comentarios d = new Comentarios();
		assertNull(d.getId_comentario());
		d.setId_comentario(3); d.setId_usuario("Luis"); d.setId_tarea(4);
		d.setTexto_comentario("adios"); d.setFecha_hora("h");
		assertEquals(Integer.valueOf(3), d.getId_comentario());
		assertEquals("Luis", d.getId_usuario());
		assertEquals(Integer.valueOf(4), d.getId_tarea());
		assertEquals("adios", d.getTexto_comentario());
		assertEquals("h", d.getFecha_hora());
	}

	@Test
	void asignaciones_constructorCompletoYSetters() {
		Asignaciones a = new Asignaciones(1, 3, 2, "2024-06-26");
		assertEquals(Integer.valueOf(1), a.getId_asignacion());
		assertEquals(Integer.valueOf(3), a.getId_usuario());
		assertEquals(Integer.valueOf(2), a.getId_tarea());
		assertEquals("2024-06-26", a.getFecha_asignacion());
		Asignaciones b = new Asignaciones();
		assertNull(b.getId_asignacion());
		b.setId_asignacion(9); b.setId_usuario(8); b.setId_tarea(7); b.setFecha_asignacion("z");
		assertEquals(Integer.valueOf(9), b.getId_asignacion());
		assertEquals(Integer.valueOf(8), b.getId_usuario());
		assertEquals(Integer.valueOf(7), b.getId_tarea());
		assertEquals("z", b.getFecha_asignacion());
	}
}
