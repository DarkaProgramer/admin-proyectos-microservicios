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

import com.mx.cbtis.gestor.dto.UsuarioDto;
import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@RestController
public class UsuarioController {

	private final UsuarioRepository usuariotable;

	public UsuarioController(UsuarioRepository usuariotable) {
		this.usuariotable = usuariotable;
	}

	@PostMapping("/getUsuarioLogin")
	public UsuarioDto getUsuarioLogin(@RequestBody UsuarioDto userParam) {
		Optional<Usuario> optionaluser = usuariotable.findByCorreoPass(
				userParam.getCorreo_electronico(), userParam.getContrasena());
		if (optionaluser.isPresent()) {
			return UsuarioDto.from(optionaluser.get());
		}
		UsuarioDto vacio = new UsuarioDto();
		vacio.setNombre("000");
		return vacio;
	}

	@GetMapping("/buscarUsuario")
	public UsuarioDto getUsuario(@RequestParam int id) {
		Optional<Usuario> encontrado = usuariotable.findById(id);
		return encontrado.map(UsuarioDto::from).orElseGet(UsuarioDto::new);
	}

	@PostMapping("/addUsuario")
	public int addUsuario(@RequestBody UsuarioDto dto) {
		int flag = 0;
		if (dto != null) {
			usuariotable.save(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/updateUsuario")
	public int updateUsuario(@RequestBody UsuarioDto dto) {
		int flag = 0;
		if (dto != null) {
			usuariotable.save(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@DeleteMapping("/deleteUsuario")
	public int deleteUsuario(@RequestBody UsuarioDto dto) {
		int flag = 0;
		if (dto != null && dto.getId_usuario() != null && usuariotable.existsById(dto.getId_usuario())) {
			usuariotable.delete(dto.toEntity());
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/buscarUsuarios")
	public List<UsuarioDto> getUsuarios() {
		return StreamSupport.stream(usuariotable.findAll().spliterator(), false).map(UsuarioDto::from).toList();
	}
}
