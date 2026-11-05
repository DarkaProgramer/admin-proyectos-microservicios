package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Tareas;

/** DTO de Tareas: evita exponer la entidad JPA en la capa REST (Sonar java:S4684). */
public class TareasDto {

	private Integer id_tarea;
	private String nombre;
	private String descripcion;
	private String fecha_vencimiento;
	private String lista;
	private String estado;
	private Integer id_proyecto;

	public TareasDto() {
		super();
	}

	public Integer getId_tarea() {
		return id_tarea;
	}

	public void setId_tarea(Integer id_tarea) {
		this.id_tarea = id_tarea;
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

	public String getFecha_vencimiento() {
		return fecha_vencimiento;
	}

	public void setFecha_vencimiento(String fecha_vencimiento) {
		this.fecha_vencimiento = fecha_vencimiento;
	}

	public String getLista() {
		return lista;
	}

	public void setLista(String lista) {
		this.lista = lista;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getId_proyecto() {
		return id_proyecto;
	}

	public void setId_proyecto(Integer id_proyecto) {
		this.id_proyecto = id_proyecto;
	}

	public static TareasDto from(Tareas e) {
		TareasDto d = new TareasDto();
		d.setId_tarea(e.getId_tarea());
		d.setNombre(e.getNombre());
		d.setDescripcion(e.getDescripcion());
		d.setFecha_vencimiento(e.getFecha_vencimiento());
		d.setLista(e.getLista());
		d.setEstado(e.getEstado());
		d.setId_proyecto(e.getId_proyecto());
		return d;
	}

	public Tareas toEntity() {
		Tareas e = new Tareas();
		e.setId_tarea(getId_tarea());
		e.setNombre(getNombre());
		e.setDescripcion(getDescripcion());
		e.setFecha_vencimiento(getFecha_vencimiento());
		e.setLista(getLista());
		e.setEstado(getEstado());
		e.setId_proyecto(getId_proyecto());
		return e;
	}
}
