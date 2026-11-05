package com.mx.cbtis.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

/**
 * Pruebas automaticas del front end (capa controlador) - Ejercicio5Cliente.
 *
 * Cada prueba reemplaza el RestTemplate que el controlador crea internamente
 * por un doble (Mockito.mockConstruction). Por eso NO se necesita la API en el
 * puerto 8081 ni MariaDB, y NO se escribe nada en la base de datos.
 *
 * Exito:   AUT-E01 a E05, E07 a E10   (AUT-E06 esta en pruebas-js/login.test.js)
 * Fracaso: AUT-F01, F02, F03, F09, F10 (AUT-F04 a F08 estan en pruebas-js/login.test.js)
 */
class GestorControllerAutomaticoTest {

    private static final String BASE = "http://localhost:8081";
    private static final String URL_BUSCAR_PROYECTOS = BASE + "/buscarProyectos";
    private static final String URL_BUSCAR_USUARIOS = BASE + "/buscarUsuarios";
    private static final String URL_GET_TAREA = BASE + "/getTarea";
    private static final String URL_GET_COMENTARIO = BASE + "/getComentario";
    private static final String URL_LOGIN = BASE + "/getUsuarioLogin";
    private static final String URL_ADD_USUARIO = BASE + "/addUsuario";
    private static final String URL_ADD_PROYECTO = BASE + "/addProyecto";
    private static final String URL_BUSCAR_PROYECTO = BASE + "/buscarProyecto?id_proyecto={id_proyecto}";

    private final GestorController controller = new GestorController();

    // ---------------------------------------------------------------- datos de apoyo

    private static Proyectos proyecto(Integer id, String nombre) {
        Proyectos p = new Proyectos();
        p.setId_proyecto(id);
        p.setNombre(nombre);
        return p;
    }

    private static Usuario usuario(Integer id, String nombre) {
        Usuario u = new Usuario();
        u.setId_usuario(id);
        u.setNombre(nombre);
        return u;
    }

    private static Tareas tarea(Integer id, String nombre, Integer idProyecto) {
        Tareas t = new Tareas();
        t.setId_tarea(id);
        t.setNombre(nombre);
        t.setId_proyecto(idProyecto);
        return t;
    }

    private static Comentarios comentario(Integer id, String texto, Integer idTarea) {
        Comentarios c = new Comentarios();
        c.setId_comentario(id);
        c.setTexto_comentario(texto);
        c.setId_tarea(idTarea);
        return c;
    }

    // ================================================================= CASOS DE EXITO

    @Test
    @DisplayName("AUT-E01 | Servicio de consulta | consultarProyectos: el backend devuelve una lista valida")
    void aut_e01_consultarProyectos_respuestaValida() {
        Proyectos[] respuestaBackend = { proyecto(197, "My API Rest"), proyecto(198, "Sistema escolar") };

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq(URL_BUSCAR_PROYECTOS), any(), eq(Proyectos[].class)))
                        .thenReturn(respuestaBackend))) {

            Model modelo = new ConcurrentModel();
            ResponseEntity<Object> respuesta = controller.consultarProyectos(modelo);

            assertEquals(200, respuesta.getStatusCode().value());
            List<?> cuerpo = (List<?>) respuesta.getBody();
            assertNotNull(cuerpo);
            assertEquals(2, cuerpo.size());
            assertTrue(modelo.asMap().containsKey("listaProyectos"));
        }
    }

    @Test
    @DisplayName("AUT-E02 | Servicio de consulta | consultarUsuarios: el backend devuelve una lista valida")
    void aut_e02_consultarUsuarios_respuestaValida() {
        Usuario[] respuestaBackend = { usuario(1, "Perla Martinez"), usuario(2, "Liliana Perez") };

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq(URL_BUSCAR_USUARIOS), any(), eq(Usuario[].class)))
                        .thenReturn(respuestaBackend))) {

            Model modelo = new ConcurrentModel();
            ResponseEntity<Object> respuesta = controller.consultarUsuarios(modelo);

            assertEquals(200, respuesta.getStatusCode().value());
            List<?> cuerpo = (List<?>) respuesta.getBody();
            assertNotNull(cuerpo);
            assertEquals(2, cuerpo.size());
            assertTrue(modelo.asMap().containsKey("listaUsuarios"));
        }
    }

    @Test
    @DisplayName("AUT-E03 | Servicio de consulta | obtenerTareas: el backend devuelve las tareas de un proyecto")
    void aut_e03_obtenerTareas_respuestaValida() {
        Tareas[] respuestaBackend = { tarea(199, "Base de datos", 197), tarea(200, "Pantallas", 197) };
        Tareas peticion = new Tareas();
        peticion.setId_proyecto(197);

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq(URL_GET_TAREA), any(), eq(Tareas[].class)))
                        .thenReturn(respuestaBackend))) {

            Model modelo = new ConcurrentModel();
            ResponseEntity<List<Tareas>> respuesta = controller.obtenerTareas(peticion, modelo);

            assertEquals(200, respuesta.getStatusCode().value());
            assertNotNull(respuesta.getBody());
            assertEquals(2, respuesta.getBody().size());
            assertEquals("Base de datos", respuesta.getBody().get(0).getNombre());
            assertTrue(modelo.asMap().containsKey("tareas"));
        }
    }

    @Test
    @DisplayName("AUT-E04 | Servicio de consulta | obtenerComentarios: el backend devuelve los comentarios de una tarea")
    void aut_e04_obtenerComentarios_respuestaValida() {
        Comentarios[] respuestaBackend = { comentario(1, "Primer comentario", 199), comentario(2, "Segundo comentario", 199) };
        Comentarios peticion = new Comentarios();
        peticion.setId_tarea(199);

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq(URL_GET_COMENTARIO), any(), eq(Comentarios[].class)))
                        .thenReturn(respuestaBackend))) {

            Model modelo = new ConcurrentModel();
            ResponseEntity<List<Comentarios>> respuesta = controller.obtenerComentarios(peticion, modelo);

            assertEquals(200, respuesta.getStatusCode().value());
            assertNotNull(respuesta.getBody());
            assertEquals(2, respuesta.getBody().size());
            assertTrue(modelo.asMap().containsKey("comentarios"));
        }
    }

    @Test
    @DisplayName("AUT-E05 | Servicio de acceso | verificarUsuario: credenciales validas devuelven el usuario")
    void aut_e05_verificarUsuario_credencialesValidas() {
        Usuario entrada = new Usuario();
        entrada.setCorreo_electronico("perla22522@cbtis75.edu.mx");
        entrada.setContrasena("12345");
        Usuario salida = usuario(1, "Perla Martinez");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq(URL_LOGIN), any(), eq(Usuario.class)))
                        .thenReturn(ResponseEntity.ok(salida)))) {

            ResponseEntity<Usuario> respuesta = controller.verificarUsuario(entrada);

            assertEquals(200, respuesta.getStatusCode().value());
            assertNotNull(respuesta.getBody());
            assertEquals("Perla Martinez", respuesta.getBody().getNombre());
        }
    }

    // AUT-E06 (formulario de login, validacion en JavaScript): ver pruebas-js/login.test.js

    @Test
    @DisplayName("AUT-E07 | Formulario | setSaveUsuario: usuario valido se envia a /addUsuario y redirige a /login")
    void aut_e07_saveUsuario_datosValidos() {
        Usuario nuevo = new Usuario();
        nuevo.setNombre("Usuario Prueba");
        nuevo.setCorreo_electronico("prueba@cbtis75.edu.mx");
        nuevo.setContrasena("Prueba123");
        nuevo.setRol("Estudiante");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq(URL_ADD_USUARIO), any(), eq(Integer.class)))
                        .thenReturn(ResponseEntity.ok(1)))) {

            ModelAndView resultado = controller.setSaveUsuario(nuevo);

            assertEquals("redirect:/login", resultado.getViewName());
            RestTemplate usado = mc.constructed().get(0);
            verify(usado).postForEntity(URL_ADD_USUARIO, nuevo, Integer.class);
        }
    }

    @Test
    @DisplayName("AUT-E08 | Formulario | setSaveProyecto: proyecto valido se envia a /addProyecto y redirige a /inicio")
    void aut_e08_saveProyecto_datosValidos() {
        Proyectos nuevo = proyecto(null, "Proyecto Prueba");
        nuevo.setDescripcion("Proyecto de prueba automatica");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq(URL_ADD_PROYECTO), any(), eq(Integer.class)))
                        .thenReturn(ResponseEntity.ok(1)))) {

            ModelAndView resultado = controller.setSaveProyecto(nuevo);

            assertEquals("redirect:/inicio", resultado.getViewName());
            RestTemplate usado = mc.constructed().get(0);
            verify(usado).postForEntity(URL_ADD_PROYECTO, nuevo, Integer.class);
        }
    }

    @Test
    @DisplayName("AUT-E09 | Componente / Vista | getInicio: devuelve la vista inicio con el objeto proyectos")
    void aut_e09_getInicio_presentaVista() {
        Model modelo = new ConcurrentModel();

        String vista = controller.getInicio(modelo);

        assertEquals("inicio", vista);
        assertTrue(modelo.asMap().get("proyectos") instanceof Proyectos);
    }

    @Test
    @DisplayName("AUT-E10 | Componente / Vista | getProyecto: devuelve la vista proyecto con tarea, comentarios y usuarios")
    void aut_e10_getProyecto_presentaVista() {
        Usuario[] respuestaBackend = { usuario(1, "Perla Martinez"), usuario(2, "Liliana Perez") };

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq(URL_BUSCAR_USUARIOS), any(), eq(Usuario[].class)))
                        .thenReturn(respuestaBackend))) {

            Model modelo = new ConcurrentModel();
            String vista = controller.getProyecto(modelo);

            assertEquals("proyecto", vista);
            assertTrue(modelo.asMap().get("tarea") instanceof Tareas);
            assertTrue(modelo.asMap().get("comentarios") instanceof Comentarios);
            assertEquals(2, ((List<?>) modelo.asMap().get("listaUsuarios")).size());
        }
    }

    // ================================================================= CASOS DE FRACASO
    // Comportamiento esperado: ante un fallo del backend el controlador NO propaga la
    // excepcion y responde con un estado de error HTTP controlado.

    @Test
    @DisplayName("AUT-F01 | Servicio de consulta | consultarProyectos: el backend responde error 500")
    void aut_f01_consultarProyectos_backendConError() {
        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq(URL_BUSCAR_PROYECTOS), any(), eq(Proyectos[].class)))
                        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))) {

            ResponseEntity<Object> respuesta = assertDoesNotThrow(
                    () -> controller.consultarProyectos(new ConcurrentModel()),
                    "El controlador no debe propagar la excepcion del backend");

            assertTrue(respuesta.getStatusCode().is5xxServerError(),
                    "Debe responder con un estado de error controlado (5xx)");
        }
    }

    @Test
    @DisplayName("AUT-F02 | Servicio de consulta | obtenerTareas: el backend responde error 500")
    void aut_f02_obtenerTareas_backendConError() {
        Tareas peticion = new Tareas();
        peticion.setId_proyecto(197);

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq(URL_GET_TAREA), any(), eq(Tareas[].class)))
                        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))) {

            ResponseEntity<List<Tareas>> respuesta = assertDoesNotThrow(
                    () -> controller.obtenerTareas(peticion, new ConcurrentModel()),
                    "El controlador no debe propagar la excepcion del backend");

            assertTrue(respuesta.getStatusCode().is5xxServerError(),
                    "Debe responder con un estado de error controlado (5xx)");
        }
    }

    @Test
    @DisplayName("AUT-F03 | Servicio de acceso | verificarUsuario: el backend no esta disponible")
    void aut_f03_verificarUsuario_backendNoDisponible() {
        Usuario entrada = new Usuario();
        entrada.setCorreo_electronico("perla22522@cbtis75.edu.mx");
        entrada.setContrasena("12345");

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForEntity(eq(URL_LOGIN), any(), eq(Usuario.class)))
                        .thenThrow(new ResourceAccessException("Connection refused")))) {

            ResponseEntity<Usuario> respuesta = assertDoesNotThrow(
                    () -> controller.verificarUsuario(entrada),
                    "El controlador no debe propagar la excepcion de conexion");

            assertTrue(respuesta.getStatusCode().is5xxServerError(),
                    "Debe responder con un estado de error controlado (5xx)");
        }
    }

    // AUT-F04 a F08 (validaciones del formulario de login en JavaScript): ver pruebas-js/login.test.js

    @Test
    @DisplayName("AUT-F09 | Recurso inexistente | updateProyecto: el proyecto buscado no existe")
    void aut_f09_updateProyecto_recursoInexistente() {
        Proyectos peticion = new Proyectos();
        peticion.setId_proyecto(99999);
        // La API real responde 200 con un objeto Proyectos vacio cuando el id no existe.
        Proyectos respuestaVaciaDelBackend = new Proyectos();

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.getForObject(eq(URL_BUSCAR_PROYECTO), eq(Proyectos.class),
                        Mockito.<String, Object>anyMap())).thenReturn(respuestaVaciaDelBackend))) {

            ResponseEntity<Object> respuesta = assertDoesNotThrow(
                    () -> controller.updateProyecto(peticion));

            assertEquals(404, respuesta.getStatusCode().value(),
                    "Si el proyecto no existe debe responder 404 (no encontrado)");
        }
    }

    @Test
    @DisplayName("AUT-F10 | Servicio de consulta | obtenerComentarios: el backend responde error 500")
    void aut_f10_obtenerComentarios_backendConError() {
        Comentarios peticion = new Comentarios();
        peticion.setId_tarea(199);

        try (MockedConstruction<RestTemplate> mc = mockConstruction(RestTemplate.class,
                (rt, ctx) -> when(rt.postForObject(eq(URL_GET_COMENTARIO), any(), eq(Comentarios[].class)))
                        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))) {

            ResponseEntity<List<Comentarios>> respuesta = assertDoesNotThrow(
                    () -> controller.obtenerComentarios(peticion, new ConcurrentModel()),
                    "El controlador no debe propagar la excepcion del backend");

            assertTrue(respuesta.getStatusCode().is5xxServerError(),
                    "Debe responder con un estado de error controlado (5xx)");
        }
    }
}