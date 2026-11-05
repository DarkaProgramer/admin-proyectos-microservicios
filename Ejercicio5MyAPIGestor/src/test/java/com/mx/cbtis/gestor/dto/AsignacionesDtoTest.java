package com.mx.cbtis.gestor.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mx.cbtis.gestor.modelo.Asignaciones;

class AsignacionesDtoTest {

	@Test
	void toEntity_y_from_conservanTodosLosCampos() {
		AsignacionesDto d = new AsignacionesDto();
		d.setId_asignacion(7);
		d.setId_usuario(7);
		d.setId_tarea(7);
		d.setFecha_asignacion("x_fecha_asignacion");
		Asignaciones e = d.toEntity();
		assertEquals(Integer.valueOf(7), e.getId_asignacion());
		assertEquals(Integer.valueOf(7), e.getId_usuario());
		assertEquals(Integer.valueOf(7), e.getId_tarea());
		assertEquals("x_fecha_asignacion", e.getFecha_asignacion());
		AsignacionesDto r = AsignacionesDto.from(e);
		assertEquals(Integer.valueOf(7), r.getId_asignacion());
		assertEquals(Integer.valueOf(7), r.getId_usuario());
		assertEquals(Integer.valueOf(7), r.getId_tarea());
		assertEquals("x_fecha_asignacion", r.getFecha_asignacion());
	}
}
