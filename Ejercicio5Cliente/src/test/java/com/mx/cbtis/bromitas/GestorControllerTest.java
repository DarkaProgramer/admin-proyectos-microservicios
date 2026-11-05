package com.mx.cbtis.bromitas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.controller.GestorController;
import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

@SpringBootTest
public class GestorControllerTest {

    @Autowired
    private GestorController controller;

    @Test
    public void testGetHome() {
        String viewName = controller.getHome();
        assertEquals("home", viewName);
    }

    @Test
    public void testGetTutorial() {
        String viewName = controller.getTutorial();
        assertEquals("tutorial", viewName);
    }

    @Test
    public void testGetManual() {
        String viewName = controller.getManual();
        assertEquals("manual", viewName);
    }

    @Test
    public void testGetLogin() {
        String viewName = controller.getLogin();
        assertEquals("login", viewName);
    }

    @Test
    public void testGetRegister() {
        Model model = new ConcurrentModel();
        String viewName = controller.getRegister(model);

        assertEquals("register", viewName);
        assertNotNull(model.getAttribute("usuario"));
    }

    @Test
    public void testSetSaveUsuario() {
        Usuario usuario = new Usuario();

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq("http://localhost:8081/addUsuario"), any(), eq(Integer.class)))
                        .thenReturn(ResponseEntity.ok(1)))) {
            ModelAndView mav = controller.setSaveUsuario(usuario);
            assertEquals("redirect:/login", mav.getViewName());
        }
    }

    @Test
    public void testGetInicio() {
        Model model = new ConcurrentModel();
        String viewName = controller.getInicio(model);

        assertEquals("inicio", viewName);
        assertNotNull(model.getAttribute("proyectos"));
    }

    @Test
    public void testSetSaveProyecto() {
        Proyectos proyecto = new Proyectos();

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq("http://localhost:8081/addProyecto"), any(), eq(Integer.class)))
                        .thenReturn(ResponseEntity.ok(1)))) {
            ModelAndView mav = controller.setSaveProyecto(proyecto);
            assertEquals("redirect:/inicio", mav.getViewName());
        }
    }

    @Test
    public void testConsultarProyectos() {
        Model model = new ConcurrentModel();
        Proyectos p = new Proyectos();
        p.setId_proyecto(1);
        p.setNombre("Proyecto Test");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq("http://localhost:8081/buscarProyectos"), any(), eq(Proyectos[].class)))
                        .thenReturn(new Proyectos[] { p }))) {
            ResponseEntity<Object> response = controller.consultarProyectos(model);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(model.getAttribute("listaProyectos"));
        }
    }

    @Test
    public void testEliminarProyecto() {
        Proyectos proyecto = new Proyectos();
        proyecto.setId_proyecto(1);

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.exchange(eq("http://localhost:8081/deleteProyecto"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Integer.class)))
                        .thenReturn(ResponseEntity.ok(1)))) {
            ModelAndView mav = controller.eliminarProyecto(proyecto);
            assertEquals("redirect:/inicio", mav.getViewName());
        }
    }

    @Test
    public void testUpdateProyecto() {
        Proyectos proyecto = new Proyectos();
        proyecto.setId_proyecto(1);
        proyecto.setNombre("Proyecto Actualizado");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.getForObject(eq("http://localhost:8081/buscarProyecto?id_proyecto={id_proyecto}"), eq(Proyectos.class), anyMap()))
                        .thenReturn(proyecto))) {
            ResponseEntity<Object> response = controller.updateProyecto(proyecto);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void testVerificarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId_usuario(1);
        usuario.setNombre("Usuario Test");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq("http://localhost:8081/getUsuarioLogin"), any(), eq(Usuario.class)))
                        .thenReturn(ResponseEntity.ok(usuario)))) {
            ResponseEntity<Usuario> response = controller.verificarUsuario(usuario);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void testConsultarUsuarios() {
        Model model = new ConcurrentModel();
        Usuario u = new Usuario();
        u.setId_usuario(1);
        u.setNombre("User1");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq("http://localhost:8081/buscarUsuarios"), any(), eq(Usuario[].class)))
                        .thenReturn(new Usuario[] { u }))) {
            ResponseEntity<Object> response = controller.consultarUsuarios(model);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(model.getAttribute("listaUsuarios"));
        }
    }

    @Test
    public void testObtenerTareas() {
        Tareas tareaParam = new Tareas();
        tareaParam.setId_tarea(1);
        tareaParam.setNombre("Tarea 1");
        Model model = new ConcurrentModel();

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq("http://localhost:8081/getTarea"), any(), eq(Tareas[].class)))
                        .thenReturn(new Tareas[] { tareaParam }))) {
            ResponseEntity<List<Tareas>> response = controller.obtenerTareas(tareaParam, model);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(model.getAttribute("tareas"));
        }
    }

    @Test
    public void testObtenerComentarios() {
        Comentarios comentarioParam = new Comentarios();
        comentarioParam.setId_comentario(1);
        comentarioParam.setTexto_comentario("Comentario 1");
        Model model = new ConcurrentModel();

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq("http://localhost:8081/getComentario"), any(), eq(Comentarios[].class)))
                        .thenReturn(new Comentarios[] { comentarioParam }))) {
            ResponseEntity<List<Comentarios>> response = controller.obtenerComentarios(comentarioParam, model);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(model.getAttribute("comentarios"));
        }
    }

    @Test
    public void testGetProyecto() {
        Model model = new ConcurrentModel();
        Usuario u = new Usuario();
        u.setId_usuario(1);
        u.setNombre("User1");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq("http://localhost:8081/buscarUsuarios"), any(), eq(Usuario[].class)))
                        .thenReturn(new Usuario[] { u }))) {
            String viewName = controller.getProyecto(model);

            assertEquals("proyecto", viewName);
            assertNotNull(model.getAttribute("tarea"));
            assertNotNull(model.getAttribute("comentarios"));
            assertNotNull(model.getAttribute("listaUsuarios"));
        }
    }

    @Test
    public void testGetTarea() {
        Model model = new ConcurrentModel();
        String viewName = controller.getTarea(model);

        assertEquals("proyecto", viewName);
        assertNotNull(model.getAttribute("tareas"));
    }

    @Test
    public void testSetSaveTareas() {
        Tareas tareas = new Tareas();
        tareas.setId_proyecto(5);

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq("http://localhost:8081/addTareas"), any(), eq(Integer.class)))
                        .thenReturn(ResponseEntity.ok(1)))) {
            ModelAndView mav = controller.setSaveTareas(tareas);

            assertEquals("redirect:/proyecto", mav.getViewName());
            assertEquals(5, mav.getModel().get("id_proyecto"));
        }
    }

    @Test
    public void testSetSaveComentario() {
        Comentarios comentario = new Comentarios();

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq("http://localhost:8081/addComentario"), any(), eq(Integer.class)))
                        .thenReturn(ResponseEntity.ok(1)))) {
            String viewName = controller.setSaveComentario(comentario);
            assertEquals("proyecto", viewName);
        }
    }

    @Test
    public void testUpdateTarea() {
        Tareas tarea = new Tareas();
        tarea.setId_tarea(10);
        tarea.setNombre("Tarea Actualizada");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.getForObject(eq("http://localhost:8081/buscarTarea?id_tarea={id_tarea}"), eq(Tareas.class), anyMap()))
                        .thenReturn(tarea))) {
            ResponseEntity<Object> response = controller.updateTarea(tarea);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void testEliminarTarea() {
        Tareas tarea = new Tareas();
        tarea.setId_tarea(10);

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.exchange(eq("http://localhost:8081/deleteTareas"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Integer.class)))
                        .thenReturn(ResponseEntity.ok(1)))) {
            ModelAndView mav = controller.eliminarTarea(tarea);
            assertEquals("redirect:/proyecto", mav.getViewName());
        }
    }

    @Test
    public void testVerificarTarea() {
        Tareas tarea = new Tareas();

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq("http://localhost:8081/addTareas"), any(), eq(Integer.class)))
                        .thenReturn(ResponseEntity.ok(1)))) {
            ResponseEntity<Integer> response = controller.verificarTarea(tarea);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void testCambiosTareas() {
        Tareas tarea = new Tareas();

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq("http://localhost:8081/addTareas"), any(), eq(Integer.class)))
                        .thenReturn(ResponseEntity.ok(1)))) {
            ResponseEntity<Integer> response = controller.cambiosTareas(tarea);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void testConsultarTareas() {
        Tareas t = new Tareas();
        t.setId_tarea(1);
        t.setNombre("Tarea General");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq("http://localhost:8081/buscarTareas"), any(), eq(Tareas[].class)))
                        .thenReturn(new Tareas[] { t }))) {
            ResponseEntity<Object> response = controller.consultarTareas();
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void testConsultarComentarios() {
        Comentarios c = new Comentarios();
        c.setId_comentario(1);
        c.setTexto_comentario("Comentario General");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq("http://localhost:8081/buscarComentarios"), any(), eq(Comentarios[].class)))
                        .thenReturn(new Comentarios[] { c }))) {
            ResponseEntity<Object> response = controller.consultarComentarios();
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
}
