package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Proyectos;

/** DTO de Proyectos: evita exponer la entidad JPA en la capa REST (Sonar java:S4684). */
public class ProyectosDto {

	private Integer id_proyecto;
	private String nombre;
	private String descripcion;
	private String fecha_inicio;
	private String fecha_finalizacion;
	private String estado_proyecto;
	private String fondo;

	public ProyectosDto() {
		super();
	}

	public Integer getId_proyecto() {
		return id_proyecto;
	}

	public void setId_proyecto(Integer id_proyecto) {
		this.id_proyecto = id_proyecto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFecha_inicio() {
		return fecha_inicio;
	}

	public void setFecha_inicio(String fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}

	public String getFecha_finalizacion() {
		return fecha_finalizacion;
	}

	public void setFecha_finalizacion(String fecha_finalizacion) {
		this.fecha_finalizacion = fecha_finalizacion;
	}

	public String getEstado_proyecto() {
		return estado_proyecto;
	}

	public void setEstado_proyecto(String estado_proyecto) {
		this.estado_proyecto = estado_proyecto;
	}

	public String getFondo() {
		return fondo;
	}

	public void setFondo(String fondo) {
		this.fondo = fondo;
	}

	public static ProyectosDto from(Proyectos e) {
		ProyectosDto d = new ProyectosDto();
		d.setId_proyecto(e.getId_proyecto());
		d.setNombre(e.getNombre());
		d.setDescripcion(e.getDescripcion());
		d.setFecha_inicio(e.getFecha_inicio());
		d.setFecha_finalizacion(e.getFecha_finalizacion());
		d.setEstado_proyecto(e.getEstado_proyecto());
		d.setFondo(e.getFondo());
		return d;
	}

	public Proyectos toEntity() {
		Proyectos e = new Proyectos();
		e.setId_proyecto(getId_proyecto());
		e.setNombre(getNombre());
		e.setDescripcion(getDescripcion());
		e.setFecha_inicio(getFecha_inicio());
		e.setFecha_finalizacion(getFecha_finalizacion());
		e.setEstado_proyecto(getEstado_proyecto());
		e.setFondo(getFondo());
		return e;
	}
}
