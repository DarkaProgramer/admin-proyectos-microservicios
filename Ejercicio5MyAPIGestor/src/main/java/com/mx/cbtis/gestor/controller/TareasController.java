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

import com.mx.cbtis.gestor.dto.TareasDto;
import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.TareasRepository;

@RestController
public class TareasController {

	private final TareasRepository tareastable;

	public TareasController(TareasRepository tareastable) {
		this.tareastable = tareastable;
	}

	@PostMapping("/getTarea")
	public List<TareasDto> getTarea(@RequestBody TareasDto id_proyecto) {
		return tareastable.findByIdProyecto(id_proyecto.getId_proyecto()).stream().map(TareasDto::from).toList();
	}

	@GetMapping("/buscarTarea")
	public TareasDto getTarea(@RequestParam int id_tarea) {
		Optional<Tareas> encontrado = tareastable.findById(id_tarea);
		return encontrado.map(TareasDto::from).orElseGet(TareasDto::new);
	}

	@PostMapping("/addTareas")
	public int addTareas(@RequestBody TareasDto dto) {
		int flag = 0;
		if (dto != null) {
			tareastable.save(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/updateTareas")
	public int updateTareas(@RequestBody TareasDto dto) {
		int flag = 0;
		if (dto != null) {
			tareastable.save(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@DeleteMapping("/deleteTareas")
	public int deleteTareas(@RequestBody TareasDto dto) {
		int flag = 0;
		if (dto != null && dto.getId_tarea() != null && tareastable.existsById(dto.getId_tarea())) {
			tareastable.delete(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/buscarTareas")
	public List<TareasDto> getTareas() {
		return StreamSupport.stream(tareastable.findAll().spliterator(), false).map(TareasDto::from).toList();
	}
}
