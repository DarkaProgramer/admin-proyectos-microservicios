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

import com.mx.cbtis.gestor.dto.ComentariosDto;
import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;

@RestController
public class ComentariosController {

	private final ComentariosRepository comentariostable;

	public ComentariosController(ComentariosRepository comentariostable) {
		this.comentariostable = comentariostable;
	}

	@PostMapping("/getComentario")
	public List<ComentariosDto> getComentario(@RequestBody ComentariosDto idComentario) {
		return comentariostable.findByTarea(idComentario.getId_tarea()).stream().map(ComentariosDto::from).toList();
	}

	@GetMapping("/buscarComentario")
	public ComentariosDto getComentario(@RequestParam int id) {
		Optional<Comentarios> encontrado = comentariostable.findById(id);
		return encontrado.map(ComentariosDto::from).orElseGet(ComentariosDto::new);
	}

	@PostMapping("/addComentario")
	public int addComentario(@RequestBody ComentariosDto dto) {
		int flag = 0;
		if (dto != null) {
			comentariostable.save(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/updateComentario")
	public int updateComentario(@RequestBody ComentariosDto dto) {
		int flag = 0;
		if (dto != null) {
			comentariostable.save(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@DeleteMapping("/deleteComentario")
	public int deleteComentario(@RequestBody ComentariosDto dto) {
		int flag = 0;
		if (dto != null && dto.getId_comentario() != null && comentariostable.existsById(dto.getId_comentario())) {
			comentariostable.delete(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/buscarComentarios")
	public List<ComentariosDto> getComentarios() {
		return StreamSupport.stream(comentariostable.findAll().spliterator(), false).map(ComentariosDto::from).toList();
	}
}
