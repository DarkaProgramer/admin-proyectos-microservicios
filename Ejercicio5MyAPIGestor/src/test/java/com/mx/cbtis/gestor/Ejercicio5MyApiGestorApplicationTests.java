package com.mx.cbtis.gestor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mx.cbtis.gestor.controller.AsignacionesController;
import com.mx.cbtis.gestor.controller.ComentariosController;
import com.mx.cbtis.gestor.controller.ProyectosController;
import com.mx.cbtis.gestor.controller.TareasController;
import com.mx.cbtis.gestor.controller.UsuarioController;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;
import com.mx.cbtis.gestor.repository.ComentariosRepository;
import com.mx.cbtis.gestor.repository.ProtectosRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
	DataSourceAutoConfiguration.class,
	HibernateJpaAutoConfiguration.class
})
class Ejercicio5MyApiGestorApplicationTests {

	@MockBean
	private AsignacionesRepository asignacionesRepository;

	@MockBean
	private ComentariosRepository comentariosRepository;

	@MockBean
	private ProtectosRepository protectosRepository;

	@MockBean
	private TareasRepository tareasRepository;

	@MockBean
	private UsuarioRepository usuarioRepository;

	@Autowired
	private AsignacionesController asignacionesController;

	@Autowired
	private ComentariosController comentariosController;

	@Autowired
	private ProyectosController proyectosController;

	@Autowired
	private TareasController tareasController;

	@Autowired
	private UsuarioController usuarioController;

	@Test
	void contextLoads() {
		assertNotNull(asignacionesController);
		assertNotNull(comentariosController);
		assertNotNull(proyectosController);
		assertNotNull(tareasController);
		assertNotNull(usuarioController);
	}

}
