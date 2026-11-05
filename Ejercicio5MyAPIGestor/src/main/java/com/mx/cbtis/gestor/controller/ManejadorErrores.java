package com.mx.cbtis.gestor.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Convierte errores de datos invalidos de la BD (antes HTTP 500) en HTTP 400. */
@RestControllerAdvice
public class ManejadorErrores {

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<String> datosInvalidos(DataAccessException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("Datos invalidos: no se pudo procesar la operacion");
	}
}
