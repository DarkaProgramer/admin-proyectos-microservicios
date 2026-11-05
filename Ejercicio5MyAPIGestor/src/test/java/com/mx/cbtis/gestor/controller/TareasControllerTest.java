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
import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.TareasRepository;

/**
 * Pruebas unitarias de TareasController (MockMvc standalone + Mockito).
 * Cada endpoint se prueba en escenario de exito y de fallo, validando
 * codigo HTTP, cuerpo de la respuesta e interaccion con el repositorio.
 */
@ExtendWith(MockitoExtension.class)
class TareasControllerTest {

	@Mock
	private TareasRepository repo;

	@InjectMocks
	private TareasController controller;

	private MockMvc mvc;
	private final ObjectMapper json = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	private Tareas sample() {
		Tareas e = new Tareas();
		e.setId_tarea(1);
		e.setNombre("T1");
		e.setId_proyecto(5);
		e.setEstado("pendiente");
		return e;
	}

	private String sampleJson() throws Exception {
		return json.writeValueAsString(sample());
	}

	// ---------- GET /buscarTarea ----------

	@Test
	void buscar_existente_devuelveEntidad() throws Exception {
		when(repo.findById(1)).thenReturn(Optional.of(sample()));
		String body = mvc.perform(get("/buscarTarea").param("id_tarea", "1"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Tareas r = json.readValue(body, Tareas.class);
		assertEquals(Integer.valueOf(1), r.getId_tarea());
		assertEquals("T1", r.getNombre());
		verify(repo).findById(1);
	}

	@Test
	void buscar_inexistente_devuelveEntidadVacia() throws Exception {
		when(repo.findById(99)).thenReturn(Optional.empty());
		String body = mvc.perform(get("/buscarTarea").param("id_tarea", "99"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Tareas r = json.readValue(body, Tareas.class);
		assertNull(r.getId_tarea());
		assertNull(r.getNombre());
	}

	@Test
	void buscar_sinParametro_devuelve400() throws Exception {
		mvc.perform(get("/buscarTarea")).andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void buscar_parametroNoNumerico_devuelve400() throws Exception {
		mvc.perform(get("/buscarTarea").param("id_tarea", "abc")).andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	// ---------- POST /addTareas ----------

	@Test
	void agregar_valido_devuelve1YGuarda() throws Exception {
		mvc.perform(post("/addTareas").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).save(any(Tareas.class));
	}

	@Test
	void agregar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(post("/addTareas").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void agregar_jsonMalformado_devuelve400() throws Exception {
		mvc.perform(post("/addTareas").contentType(MediaType.APPLICATION_JSON).content("{no es json"))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void agregar_nulo_devuelve0SinGuardar() {
		assertEquals(0, controller.addTareas(null));
		verify(repo, never()).save(any());
	}

	// ---------- POST /updateTareas ----------

	@Test
	void actualizar_valido_devuelve1YGuarda() throws Exception {
		mvc.perform(post("/updateTareas").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).save(any(Tareas.class));
	}

	@Test
	void actualizar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(post("/updateTareas").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void actualizar_nulo_devuelve0SinGuardar() {
		assertEquals(0, controller.updateTareas(null));
		verify(repo, never()).save(any());
	}

	// ---------- DELETE /deleteTareas ----------

	@Test
	void eliminar_valido_devuelve1YElimina() throws Exception {
		when(repo.existsById(1)).thenReturn(true);
		mvc.perform(delete("/deleteTareas").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		verify(repo).delete(any(Tareas.class));
	}

	@Test
	void eliminar_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(delete("/deleteTareas").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

	@Test
	void eliminar_nulo_devuelve0SinEliminar() {
		assertEquals(0, controller.deleteTareas(null));
		verify(repo, never()).delete(any());
	}

	@Test
	void eliminar_idInexistente_devuelve0SinEliminar() throws Exception {
		when(repo.existsById(1)).thenReturn(false);
		mvc.perform(delete("/deleteTareas").contentType(MediaType.APPLICATION_JSON).content(sampleJson()))
				.andExpect(status().isOk()).andExpect(content().string("0"));
		verify(repo, never()).delete(any());
	}

	@Test
	void eliminar_sinId_devuelve0SinEliminar() throws Exception {
		mvc.perform(delete("/deleteTareas").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isOk()).andExpect(content().string("0"));
		verify(repo, never()).delete(any());
	}

	// ---------- POST /buscarTareas ----------

	@Test
	void listar_devuelveTodosLosRegistros() throws Exception {
		when(repo.findAll()).thenReturn(Arrays.asList(sample(), sample()));
		mvc.perform(post("/buscarTareas")).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id_tarea").value(1));
	}

	@Test
	void listar_sinRegistros_devuelveArregloVacio() throws Exception {
		when(repo.findAll()).thenReturn(Collections.emptyList());
		mvc.perform(post("/buscarTareas")).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	// ---------- POST /getTarea (tareas por proyecto) ----------

	@Test
	void tareasPorProyecto_devuelveSoloLasDelProyecto() throws Exception {
		when(repo.findByIdProyecto(5)).thenReturn(Arrays.asList(sample()));
		mvc.perform(post("/getTarea").contentType(MediaType.APPLICATION_JSON).content("{\"id_proyecto\":5}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nombre").value("T1"));
		verify(repo).findByIdProyecto(5);
	}

	@Test
	void tareasPorProyecto_sinTareas_devuelveArregloVacio() throws Exception {
		when(repo.findByIdProyecto(99)).thenReturn(Collections.emptyList());
		mvc.perform(post("/getTarea").contentType(MediaType.APPLICATION_JSON).content("{\"id_proyecto\":99}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void tareasPorProyecto_sinCuerpo_devuelve400() throws Exception {
		mvc.perform(post("/getTarea").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(repo);
	}

}
