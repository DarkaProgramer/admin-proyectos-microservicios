package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Asignaciones;

/** DTO de Asignaciones: evita exponer la entidad JPA en la capa REST (Sonar java:S4684). */
public class AsignacionesDto {

	private Integer id_asignacion;
	private Integer id_usuario;
	private Integer id_tarea;
	private String fecha_asignacion;

	public AsignacionesDto() {
		super();
	}

	public Integer getId_asignacion() {
		return id_asignacion;
	}

	public void setId_asignacion(Integer id_asignacion) {
		this.id_asignacion = id_asignacion;
	}

	public Integer getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(Integer id_usuario) {
		this.id_usuario = id_usuario;
	}

	public Integer getId_tarea() {
		return id_tarea;
	}

	public void setId_tarea(Integer id_tarea) {
		this.id_tarea = id_tarea;
	}

	public String getFecha_asignacion() {
		return fecha_asignacion;
	}

	public void setFecha_asignacion(String fecha_asignacion) {
		this.fecha_asignacion = fecha_asignacion;
	}

	public static AsignacionesDto from(Asignaciones e) {
		AsignacionesDto d = new AsignacionesDto();
		d.setId_asignacion(e.getId_asignacion());
		d.setId_usuario(e.getId_usuario());
		d.setId_tarea(e.getId_tarea());
		d.setFecha_asignacion(e.getFecha_asignacion());
		return d;
	}

	public Asignaciones toEntity() {
		Asignaciones e = new Asignaciones();
		e.setId_asignacion(getId_asignacion());
		e.setId_usuario(getId_usuario());
		e.setId_tarea(getId_tarea());
		e.setFecha_asignacion(getFecha_asignacion());
		return e;
	}
}
