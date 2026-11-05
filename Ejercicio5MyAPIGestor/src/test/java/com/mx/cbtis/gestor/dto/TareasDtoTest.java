package com.mx.cbtis.gestor.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mx.cbtis.gestor.modelo.Tareas;

class TareasDtoTest {

	@Test
	void toEntity_y_from_conservanTodosLosCampos() {
		TareasDto d = new TareasDto();
		d.setId_tarea(7);
		d.setNombre("x_nombre");
		d.setDescripcion("x_descripcion");
		d.setFecha_vencimiento("x_fecha_vencimiento");
		d.setLista("x_lista");
		d.setEstado("x_estado");
		d.setId_proyecto(7);
		Tareas e = d.toEntity();
		assertEquals(Integer.valueOf(7), e.getId_tarea());
		assertEquals("x_nombre", e.getNombre());
		assertEquals("x_descripcion", e.getDescripcion());
		assertEquals("x_fecha_vencimiento", e.getFecha_vencimiento());
		assertEquals("x_lista", e.getLista());
		assertEquals("x_estado", e.getEstado());
		assertEquals(Integer.valueOf(7), e.getId_proyecto());
		TareasDto r = TareasDto.from(e);
		assertEquals(Integer.valueOf(7), r.getId_tarea());
		assertEquals("x_nombre", r.getNombre());
		assertEquals("x_descripcion", r.getDescripcion());
		assertEquals("x_fecha_vencimiento", r.getFecha_vencimiento());
		assertEquals("x_lista", r.getLista());
		assertEquals("x_estado", r.getEstado());
		assertEquals(Integer.valueOf(7), r.getId_proyecto());
	}
}
