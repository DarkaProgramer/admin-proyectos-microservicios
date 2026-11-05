package com.mx.cbtis.gestor.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mx.cbtis.gestor.modelo.Comentarios;

class ComentariosDtoTest {

	@Test
	void toEntity_y_from_conservanTodosLosCampos() {
		ComentariosDto d = new ComentariosDto();
		d.setId_comentario(7);
		d.setId_usuario("x_id_usuario");
		d.setId_tarea(7);
		d.setTexto_comentario("x_texto_comentario");
		d.setFecha_hora("x_fecha_hora");
		Comentarios e = d.toEntity();
		assertEquals(Integer.valueOf(7), e.getId_comentario());
		assertEquals("x_id_usuario", e.getId_usuario());
		assertEquals(Integer.valueOf(7), e.getId_tarea());
		assertEquals("x_texto_comentario", e.getTexto_comentario());
		assertEquals("x_fecha_hora", e.getFecha_hora());
		ComentariosDto r = ComentariosDto.from(e);
		assertEquals(Integer.valueOf(7), r.getId_comentario());
		assertEquals("x_id_usuario", r.getId_usuario());
		assertEquals(Integer.valueOf(7), r.getId_tarea());
		assertEquals("x_texto_comentario", r.getTexto_comentario());
		assertEquals("x_fecha_hora", r.getFecha_hora());
	}
}
