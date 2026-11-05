package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Comentarios;

/** DTO de Comentarios: evita exponer la entidad JPA en la capa REST (Sonar java:S4684). */
public class ComentariosDto {

	private Integer id_comentario;
	private String id_usuario;
	private Integer id_tarea;
	private String texto_comentario;
	private String fecha_hora;

	public ComentariosDto() {
		super();
	}

	public Integer getId_comentario() {
		return id_comentario;
	}

	public void setId_comentario(Integer id_comentario) {
		this.id_comentario = id_comentario;
	}

	public String getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(String id_usuario) {
		this.id_usuario = id_usuario;
	}

	public Integer getId_tarea() {
		return id_tarea;
	}

	public void setId_tarea(Integer id_tarea) {
		this.id_tarea = id_tarea;
	}

	public String getTexto_comentario() {
		return texto_comentario;
	}

	public void setTexto_comentario(String texto_comentario) {
		this.texto_comentario = texto_comentario;
	}

	public String getFecha_hora() {
		return fecha_hora;
	}

	public void setFecha_hora(String fecha_hora) {
		this.fecha_hora = fecha_hora;
	}

	public static ComentariosDto from(Comentarios e) {
		ComentariosDto d = new ComentariosDto();
		d.setId_comentario(e.getId_comentario());
		d.setId_usuario(e.getId_usuario());
		d.setId_tarea(e.getId_tarea());
		d.setTexto_comentario(e.getTexto_comentario());
		d.setFecha_hora(e.getFecha_hora());
		return d;
	}

	public Comentarios toEntity() {
		Comentarios e = new Comentarios();
		e.setId_comentario(getId_comentario());
		e.setId_usuario(getId_usuario());
		e.setId_tarea(getId_tarea());
		e.setTexto_comentario(getTexto_comentario());
		e.setFecha_hora(getFecha_hora());
		return e;
	}
}
