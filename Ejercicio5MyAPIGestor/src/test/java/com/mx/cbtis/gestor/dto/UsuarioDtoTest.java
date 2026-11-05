package com.mx.cbtis.gestor.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mx.cbtis.gestor.modelo.Usuario;

class UsuarioDtoTest {

	@Test
	void toEntity_y_from_conservanTodosLosCampos() {
		UsuarioDto d = new UsuarioDto();
		d.setId_usuario(7);
		d.setNombre("x_nombre");
		d.setCorreo_electronico("x_correo_electronico");
		d.setContrasena("x_contrasena");
		d.setRol("x_rol");
		d.setImagen_usuario("x_imagen_usuario");
		Usuario e = d.toEntity();
		assertEquals(Integer.valueOf(7), e.getId_usuario());
		assertEquals("x_nombre", e.getNombre());
		assertEquals("x_correo_electronico", e.getCorreo_electronico());
		assertEquals("x_contrasena", e.getContrasena());
		assertEquals("x_rol", e.getRol());
		assertEquals("x_imagen_usuario", e.getImagen_usuario());
		UsuarioDto r = UsuarioDto.from(e);
		assertEquals(Integer.valueOf(7), r.getId_usuario());
		assertEquals("x_nombre", r.getNombre());
		assertEquals("x_correo_electronico", r.getCorreo_electronico());
		assertEquals("x_contrasena", r.getContrasena());
		assertEquals("x_rol", r.getRol());
		assertEquals("x_imagen_usuario", r.getImagen_usuario());
	}
}
