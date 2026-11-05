package com.mx.cbtis.gestor.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mx.cbtis.gestor.modelo.Proyectos;

class ProyectosDtoTest {

	@Test
	void toEntity_y_from_conservanTodosLosCampos() {
		ProyectosDto d = new ProyectosDto();
		d.setId_proyecto(7);
		d.setNombre("x_nombre");
		d.setDescripcion("x_descripcion");
		d.setFecha_inicio("x_fecha_inicio");
		d.setFecha_finalizacion("x_fecha_finalizacion");
		d.setEstado_proyecto("x_estado_proyecto");
		d.setFondo("x_fondo");
		Proyectos e = d.toEntity();
		assertEquals(Integer.valueOf(7), e.getId_proyecto());
		assertEquals("x_nombre", e.getNombre());
		assertEquals("x_descripcion", e.getDescripcion());
		assertEquals("x_fecha_inicio", e.getFecha_inicio());
		assertEquals("x_fecha_finalizacion", e.getFecha_finalizacion());
		assertEquals("x_estado_proyecto", e.getEstado_proyecto());
		assertEquals("x_fondo", e.getFondo());
		ProyectosDto r = ProyectosDto.from(e);
		assertEquals(Integer.valueOf(7), r.getId_proyecto());
		assertEquals("x_nombre", r.getNombre());
		assertEquals("x_descripcion", r.getDescripcion());
		assertEquals("x_fecha_inicio", r.getFecha_inicio());
		assertEquals("x_fecha_finalizacion", r.getFecha_finalizacion());
		assertEquals("x_estado_proyecto", r.getEstado_proyecto());
		assertEquals("x_fondo", r.getFondo());
	}
}
