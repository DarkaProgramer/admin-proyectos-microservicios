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
import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

/**
 * Pruebas unitarias de ProyectosController (MockMvc standalone + Mockito).
 * Cada endpoint se prueba en escenario de exito y de fallo, validando
 * codigo HTTP, cuerpo de la respuesta e interaccion con el repositorio.
 */
@ExtendWith(MockitoExtension.class)
class ProyectosControllerTest {

	@Mock
	private ProtectosRepository repo;

	@InjectMocks
	private ProyectosController controller;

	private MockMvc mvc;
	private final ObjectMapper json = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	private Proyectos sample() {
		Proyectos e = new Proyectos();
		e.setId_proyecto(1);
		e.setNombre("Proyecto A");
		e.setDescripcion("desc");
		e.setEstado_proyecto("activo");
		return e;
	}

	private String sampleJson() throws Exception {
		return json.writeValueAsString(sample());
	}

	// ---------- GET /buscarProyecto ----------

	@Test
	void buscar_existente_devuelveEntidad() throws Exception {
		when(repo.findById(1)).thenReturn(Optional.of(sample()));
		String body = mvc.perform(get("/buscarProyecto").param("id_proyecto", "1"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Proyectos r = json.readValue(body, Proyectos.class);
		assertEquals(Integer.valueOf(1), r.getId_proyecto());
		assertEquals("Proyecto A", r.getNombre());
		verify(repo).findById(1);
	}

	@Test
	void buscar_inexistente_devuelveEntidadVacia() throws Exception {
		when(repo.findById(99)).thenReturn(Optional.empty());
		String body = mvc.perform(get("/buscarProyecto").param("id_proyecto", "99"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Proyectos r = json.readValue(body, Proyectos.class);
		assertNull(r.getId_proyecto());
		assertNull(r.getNombre());
	}

	@Test
	void buscar_sinParametro_devuelve400() throws Exception {
		mvc.perform(get("/buscarProyecto")).andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void buscar_parametroNoNumerico_devuelve400() throws Exception {
		mvc.perform(get("/buscarProyecto").param("id_proyecto", "abc")).andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	// ---------- POST /addProyecto ----------

	@Test
	void agregar_valido_devuelve1YGuarda() throws Exception {
		mvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).save(any(Proyectos.class));
	}

	@Test
	void agregar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void agregar_jsonMalformado_devuelve400() throws Exception {
		mvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON).content("{no es json"))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void agregar_nulo_devuelve0SinGuardar() {
		assertEquals(0, controller.addProyecto(null));
		verify(repo, never()).save(any());
	}

	// ---------- POST /updateProyecto ----------

	@Test
	void actualizar_valido_devuelve1YGuarda() throws Exception {
		mvc.perform(post("/updateProyecto").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).save(any(Proyectos.class));
	}

	@Test
	void actualizar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(post("/updateProyecto").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void actualizar_nulo_devuelve0SinGuardar() {
		assertEquals(0, controller.updateProyecto(null));
		verify(repo, never()).save(any());
	}

	// ---------- DELETE /deleteProyecto ----------

	@Test
	void eliminar_valido_devuelve1YElimina() throws Exception {
		when(repo.existsById(1)).thenReturn(true);
		mvc.perform(delete("/deleteProyecto").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).delete(any(Proyectos.class));
	}

	@Test
	void eliminar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(delete("/deleteProyecto").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void eliminar_nulo_devuelve0SinEliminar() {
		assertEquals(0, controller.deleteProyecto(null));
		verify(repo, never()).delete(any());
	}

	@Test
	void eliminar_idInexistente_devuelve0SinEliminar() throws Exception {
		when(repo.existsById(1)).thenReturn(false);
		mvc.perform(delete("/deleteProyecto").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("0"));
		verify(repo, never()).delete(any());
	}

	@Test
	void eliminar_sinId_devuelve0SinEliminar() throws Exception {
		mvc.perform(delete("/deleteProyecto").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isOk()).andExpect(content().string("0"));
		verify(repo, never()).delete(any());
	}

	// ---------- POST /buscarProyectos ----------

	@Test
	void listar_devuelveTodosLosRegistros() throws Exception {
		when(repo.findAll()).thenReturn(Arrays.asList(sample(), sample()));
		mvc.perform(post("/buscarProyectos")).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id_proyecto").value(1));
	}

	@Test
	void listar_sinRegistros_devuelveArregloVacio() throws Exception {
		when(repo.findAll()).thenReturn(Collections.emptyList());
		mvc.perform(post("/buscarProyectos")).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

}
