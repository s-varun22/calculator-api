package com.varun.calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.varun.calculator.controller.CalculatorController;
import com.varun.calculator.dto.Response;

@SpringBootTest
class CalculatorApplicationTests {

	@Autowired
	private CalculatorController calculatorController;

	@Test
	public void contextLoads() throws Exception {
		assertThat(calculatorController).isNotNull();
	}

	@Test
	public void validResponse() {
		ResponseEntity<Response> res = calculatorController.calculate("MiAqICgyMy8oMyozKSktIDIzICogKDIqMyk");
		assertEquals(HttpStatus.OK, res.getStatusCode());
	}

	@Test
	public void emptyQuery() {
		ResponseEntity<Response> res = calculatorController.calculate("IA");
		assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
		assertEquals("Empty Expression", res.getBody().getError());
	}

	@Test
	public void syntaxErrorQuery() {
		ResponseEntity<Response> res = calculatorController.calculate("KDgxIC8gMyAqMzQgZGZzIDQpIC0gMiAqIDMgKyA0");
		assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
		assertEquals("Invalid Expression", res.getBody().getError());
	}

	@Test
	public void invalidExpressionException() {
		ResponseEntity<Response> res = calculatorController.calculate("KDgxIC8gMyAqMyAqKiA0KSAtIDIgKiAzICsgNA");
		assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
		assertEquals("Syntax Error Detected", res.getBody().getError());
	}

	@Test
	public void decodeErrorHandler() {
		ResponseEntity<Response> res = calculatorController.calculate("XXXXXaGVsbG8");
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
		assertEquals("Error Occured while processing the request", res.getBody().getError());
	}
}
