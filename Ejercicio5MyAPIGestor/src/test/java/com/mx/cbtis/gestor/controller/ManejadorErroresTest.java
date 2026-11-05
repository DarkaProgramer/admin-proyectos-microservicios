package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class ManejadorErroresTest {

	@Mock
	private UsuarioRepository repo;

	@InjectMocks
	private UsuarioController controller;

	@Test
	void datosInvalidos_devuelve400ConMensaje() {
		ResponseEntity<String> r = new ManejadorErrores().datosInvalidos(new DataIntegrityViolationException("x"));
		assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
		assertEquals("Datos invalidos: no se pudo procesar la operacion", r.getBody());
	}

	@Test
	void errorDeBaseDeDatos_enEndpoint_devuelve400() throws Exception {
		when(repo.save(any(Usuario.class))).thenThrow(new DataIntegrityViolationException("fecha invalida"));
		MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ManejadorErrores()).build();
		mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"x\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Datos invalidos: no se pudo procesar la operacion"));
	}
}
