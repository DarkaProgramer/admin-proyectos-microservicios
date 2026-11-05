package com.mx.cbtis.gestor.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

/**
 * Pruebas unitarias de UsuarioController (MockMvc standalone + Mockito).
 * Cada endpoint se prueba en escenario de exito y de fallo, validando
 * codigo HTTP, cuerpo de la respuesta e interaccion con el repositorio.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

	@Mock
	private UsuarioRepository repo;

	@InjectMocks
	private UsuarioController controller;

	private MockMvc mvc;
	private final ObjectMapper json = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	private Usuario sample() {
		Usuario e = new Usuario();
		e.setId_usuario(1);
		e.setNombre("Ana");
		e.setCorreo_electronico("ana@test.com");
		e.setContrasena("1234");
		e.setRol("admin");
		return e;
	}

	private String sampleJson() throws Exception {
		return json.writeValueAsString(sample());
	}

	// ---------- GET /buscarUsuario ----------

	@Test
	void buscar_existente_devuelveEntidad() throws Exception {
		when(repo.findById(1)).thenReturn(Optional.of(sample()));
		String body = mvc.perform(get("/buscarUsuario").param("id", "1"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Usuario r = json.readValue(body, Usuario.class);
		assertEquals(Integer.valueOf(1), r.getId_usuario());
		assertEquals("Ana", r.getNombre());
		verify(repo).findById(1);
	}

	@Test
	void buscar_inexistente_devuelveEntidadVacia() throws Exception {
		when(repo.findById(99)).thenReturn(Optional.empty());
		String body = mvc.perform(get("/buscarUsuario").param("id", "99"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Usuario r = json.readValue(body, Usuario.class);
		assertNull(r.getId_usuario());
		assertNull(r.getNombre());
	}

	@Test
	void buscar_sinParametro_devuelve400() throws Exception {
		mvc.perform(get("/buscarUsuario")).andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void buscar_parametroNoNumerico_devuelve400() throws Exception {
		mvc.perform(get("/buscarUsuario").param("id", "abc")).andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	// ---------- POST /addUsuario ----------

	@Test
	void agregar_valido_devuelve1YGuarda() throws Exception {
		mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).save(any(Usuario.class));
	}

	@Test
	void agregar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void agregar_jsonMalformado_devuelve400() throws Exception {
		mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON).content("{no es json"))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void agregar_nulo_devuelve0SinGuardar() {
		assertEquals(0, controller.addUsuario(null));
		verify(repo, never()).save(any());
	}

	// ---------- POST /updateUsuario ----------

	@Test
	void actualizar_valido_devuelve1YGuarda() throws Exception {
		mvc.perform(post("/updateUsuario").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).save(any(Usuario.class));
	}

	@Test
	void actualizar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(post("/updateUsuario").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void actualizar_nulo_devuelve0SinGuardar() {
		assertEquals(0, controller.updateUsuario(null));
		verify(repo, never()).save(any());
	}

	// ---------- DELETE /deleteUsuario ----------

	@Test
	void eliminar_valido_devuelve1YElimina() throws Exception {
		when(repo.existsById(1)).thenReturn(true);
		mvc.perform(delete("/deleteUsuario").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).delete(any(Usuario.class));
	}

	@Test
	void eliminar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(delete("/deleteUsuario").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void eliminar_nulo_devuelve0SinEliminar() {
		assertEquals(0, controller.deleteUsuario(null));
		verify(repo, never()).delete(any());
	}

	@Test
	void eliminar_idInexistente_devuelve0SinEliminar() throws Exception {
		when(repo.existsById(1)).thenReturn(false);
		mvc.perform(delete("/deleteUsuario").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("0"));
		verify(repo, never()).delete(any());
	}

	@Test
	void eliminar_sinId_devuelve0SinEliminar() throws Exception {
		mvc.perform(delete("/deleteUsuario").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isOk()).andExpect(content().string("0"));
		verify(repo, never()).delete(any());
	}

	// ---------- POST /buscarUsuarios ----------

	@Test
	void listar_devuelveTodosLosRegistros() throws Exception {
		when(repo.findAll()).thenReturn(Arrays.asList(sample(), sample()));
		mvc.perform(post("/buscarUsuarios")).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id_usuario").value(1));
	}

	@Test
	void listar_sinRegistros_devuelveArregloVacio() throws Exception {
		when(repo.findAll()).thenReturn(Collections.emptyList());
		mvc.perform(post("/buscarUsuarios")).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	// ---------- POST /getUsuarioLogin ----------

	@Test
	void login_credencialesValidas_devuelveUsuario() throws Exception {
		when(repo.findByCorreoPass("ana@test.com", "1234")).thenReturn(Optional.of(sample()));
		String body = mvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON)
				.content("{\"correo_electronico\":\"ana@test.com\",\"contrasena\":\"1234\"}"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Usuario r = json.readValue(body, Usuario.class);
		assertEquals("Ana", r.getNombre());
		assertEquals(Integer.valueOf(1), r.getId_usuario());
	}

	@Test
	void login_credencialesInvalidas_devuelveMarca000() throws Exception {
		when(repo.findByCorreoPass("x@test.com", "mal")).thenReturn(Optional.empty());
		String body = mvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON)
				.content("{\"correo_electronico\":\"x@test.com\",\"contrasena\":\"mal\"}"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Usuario r = json.readValue(body, Usuario.class);
		assertEquals("000", r.getNombre());
		assertNull(r.getId_usuario());
	}

	@Test
	void login_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

}
