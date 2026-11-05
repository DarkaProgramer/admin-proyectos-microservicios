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
import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;

/**
 * Pruebas unitarias de AsignacionesController (MockMvc standalone + Mockito).
 * Cada endpoint se prueba en escenario de exito y de fallo, validando
 * codigo HTTP, cuerpo de la respuesta e interaccion con el repositorio.
 */
@ExtendWith(MockitoExtension.class)
class AsignacionesControllerTest {

	@Mock
	private AsignacionesRepository repo;

	@InjectMocks
	private AsignacionesController controller;

	private MockMvc mvc;
	private final ObjectMapper json = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	private Asignaciones sample() {
		Asignaciones e = new Asignaciones();
		e.setId_asignacion(1);
		e.setId_usuario(3);
		e.setId_tarea(2);
		e.setFecha_asignacion("2024-06-26");
		return e;
	}

	private String sampleJson() throws Exception {
		return json.writeValueAsString(sample());
	}

	// ---------- GET /buscarAsignacion ----------

	@Test
	void buscar_existente_devuelveEntidad() throws Exception {
		when(repo.findById(1)).thenReturn(Optional.of(sample()));
		String body = mvc.perform(get("/buscarAsignacion").param("id", "1"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Asignaciones r = json.readValue(body, Asignaciones.class);
		assertEquals(Integer.valueOf(1), r.getId_asignacion());
		assertEquals("2024-06-26", r.getFecha_asignacion());
		verify(repo).findById(1);
	}

	@Test
	void buscar_inexistente_devuelveEntidadVacia() throws Exception {
		when(repo.findById(99)).thenReturn(Optional.empty());
		String body = mvc.perform(get("/buscarAsignacion").param("id", "99"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Asignaciones r = json.readValue(body, Asignaciones.class);
		assertNull(r.getId_asignacion());
		assertNull(r.getFecha_asignacion());
	}

	@Test
	void buscar_sinParametro_devuelve400() throws Exception {
		mvc.perform(get("/buscarAsignacion")).andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void buscar_parametroNoNumerico_devuelve400() throws Exception {
		mvc.perform(get("/buscarAsignacion").param("id", "abc")).andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	// ---------- POST /addAsignacion ----------

	@Test
	void agregar_valido_devuelve1YGuarda() throws Exception {
		mvc.perform(post("/addAsignacion").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).save(any(Asignaciones.class));
	}

	@Test
	void agregar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(post("/addAsignacion").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void agregar_jsonMalformado_devuelve400() throws Exception {
		mvc.perform(post("/addAsignacion").contentType(MediaType.APPLICATION_JSON).content("{no es json"))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void agregar_nulo_devuelve0SinGuardar() {
		assertEquals(0, controller.addAsignacion(null));
		verify(repo, never()).save(any());
	}

	// ---------- POST /updateAsignacion ----------

	@Test
	void actualizar_valido_devuelve1YGuarda() throws Exception {
		mvc.perform(post("/updateAsignacion").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).save(any(Asignaciones.class));
	}

	@Test
	void actualizar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(post("/updateAsignacion").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void actualizar_nulo_devuelve0SinGuardar() {
		assertEquals(0, controller.updateAsignacion(null));
		verify(repo, never()).save(any());
	}

	// ---------- DELETE /deleteAsignacion ----------

	@Test
	void eliminar_valido_devuelve1YElimina() throws Exception {
		when(repo.existsById(1)).thenReturn(true);
		mvc.perform(delete("/deleteAsignacion").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).delete(any(Asignaciones.class));
	}

	@Test
	void eliminar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(delete("/deleteAsignacion").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void eliminar_nulo_devuelve0SinEliminar() {
		assertEquals(0, controller.deleteAsignacion(null));
		verify(repo, never()).delete(any());
	}

	@Test
	void eliminar_idInexistente_devuelve0SinEliminar() throws Exception {
		when(repo.existsById(1)).thenReturn(false);
		mvc.perform(delete("/deleteAsignacion").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("0"));
		verify(repo, never()).delete(any());
	}

	@Test
	void eliminar_sinId_devuelve0SinEliminar() throws Exception {
		mvc.perform(delete("/deleteAsignacion").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isOk()).andExpect(content().string("0"));
		verify(repo, never()).delete(any());
	}

	// ---------- POST /buscarAsignaciones ----------

	@Test
	void listar_devuelveTodosLosRegistros() throws Exception {
		when(repo.findAll()).thenReturn(Arrays.asList(sample(), sample()));
		mvc.perform(post("/buscarAsignaciones")).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id_asignacion").value(1));
	}

	@Test
	void listar_sinRegistros_devuelveArregloVacio() throws Exception {
		when(repo.findAll()).thenReturn(Collections.emptyList());
		mvc.perform(post("/buscarAsignaciones")).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

}
