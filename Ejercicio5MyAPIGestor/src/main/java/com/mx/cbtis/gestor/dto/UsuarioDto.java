package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Usuario;

/** DTO de Usuario: evita exponer la entidad JPA en la capa REST (Sonar java:S4684). */
public class UsuarioDto {

	private Integer id_usuario;
	private String nombre;
	private String correo_electronico;
	private String contrasena;
	private String rol;
	private String imagen_usuario;

	public UsuarioDto() {
		super();
	}

	public Integer getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(Integer id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCorreo_electronico() {
		return correo_electronico;
	}

	public void setCorreo_electronico(String correo_electronico) {
		this.correo_electronico = correo_electronico;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getImagen_usuario() {
		return imagen_usuario;
	}

	public void setImagen_usuario(String imagen_usuario) {
		this.imagen_usuario = imagen_usuario;
	}

	public static UsuarioDto from(Usuario e) {
		UsuarioDto d = new UsuarioDto();
		d.setId_usuario(e.getId_usuario());
		d.setNombre(e.getNombre());
		d.setCorreo_electronico(e.getCorreo_electronico());
		d.setContrasena(e.getContrasena());
		d.setRol(e.getRol());
		d.setImagen_usuario(e.getImagen_usuario());
		return d;
	}

	public Usuario toEntity() {
		Usuario e = new Usuario();
		e.setId_usuario(getId_usuario());
		e.setNombre(getNombre());
		e.setCorreo_electronico(getCorreo_electronico());
		e.setContrasena(getContrasena());
		e.setRol(getRol());
		e.setImagen_usuario(getImagen_usuario());
		return e;
	}
}
