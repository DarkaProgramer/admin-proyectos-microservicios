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

import com.mx.cbtis.gestor.dto.AsignacionesDto;
import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;

@RestController
public class AsignacionesController {

	private final AsignacionesRepository asignacionestable;

	public AsignacionesController(AsignacionesRepository asignacionestable) {
		this.asignacionestable = asignacionestable;
	}

	@GetMapping("/buscarAsignacion")
	public AsignacionesDto getAsignacion(@RequestParam int id) {
		Optional<Asignaciones> encontrado = asignacionestable.findById(id);
		return encontrado.map(AsignacionesDto::from).orElseGet(AsignacionesDto::new);
	}

	@PostMapping("/addAsignacion")
	public int addAsignacion(@RequestBody AsignacionesDto dto) {
		int flag = 0;
		if (dto != null) {
			asignacionestable.save(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/updateAsignacion")
	public int updateAsignacion(@RequestBody AsignacionesDto dto) {
		int flag = 0;
		if (dto != null) {
			asignacionestable.save(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@DeleteMapping("/deleteAsignacion")
	public int deleteAsignacion(@RequestBody AsignacionesDto dto) {
		int flag = 0;
		if (dto != null && dto.getId_asignacion() != null && asignacionestable.existsById(dto.getId_asignacion())) {
			asignacionestable.delete(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/buscarAsignaciones")
	public List<AsignacionesDto> getAsignaciones() {
		return StreamSupport.stream(asignacionestable.findAll().spliterator(), false).map(AsignacionesDto::from).toList();
	}
}
