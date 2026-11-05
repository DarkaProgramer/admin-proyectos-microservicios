package com.mx.cbtis.gestor.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mx.cbtis.gestor.dto.ProyectosDto;
import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@RestController
public class ProyectosController {

	private final ProtectosRepository proyectostable;

	public ProyectosController(ProtectosRepository proyectostable) {
		this.proyectostable = proyectostable;
	}

	@GetMapping("/buscarProyecto")
	public ProyectosDto getProyecto(@RequestParam int id_proyecto) {
		Optional<Proyectos> encontrado = proyectostable.findById(id_proyecto);
		return encontrado.map(ProyectosDto::from).orElseGet(ProyectosDto::new);
	}

	@PostMapping("/addProyecto")
	public int addProyecto(@RequestBody ProyectosDto dto) {
		int flag = 0;
		if (dto != null) {
			proyectostable.save(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/updateProyecto")
	public int updateProyecto(@RequestBody ProyectosDto dto) {
		int flag = 0;
		if (dto != null) {
			proyectostable.save(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@DeleteMapping("/deleteProyecto")
	public int deleteProyecto(@RequestBody ProyectosDto dto) {
		int flag = 0;
		if (dto != null && dto.getId_proyecto() != null && proyectostable.existsById(dto.getId_proyecto())) {
			proyectostable.delete(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/buscarProyectos")
	public List<ProyectosDto> getProyectos() {
		return StreamSupport.stream(proyectostable.findAll().spliterator(), false).map(ProyectosDto::from).toList();
	}
}
