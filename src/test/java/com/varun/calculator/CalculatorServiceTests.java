package com.varun.calculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.varun.calculator.exception.InvalidExpressionException;
import com.varun.calculator.exception.InvalidSyntaxException;
import com.varun.calculator.service.CalculatorService;

@SpringBootTest
public class CalculatorServiceTests {

	@Autowired
	private CalculatorService calculatorService;

	@Test
	void validQuery() throws InvalidExpressionException, InvalidSyntaxException {
		assertEquals("102.0", calculatorService.calculateResult("(81/3*4)-2*3"));
	}

	@Test
	void queryWithWhiteSpaces() throws InvalidExpressionException, InvalidSyntaxException {
		assertEquals("82.0", calculatorService.calculateResult("(81 / 3 * 4) - 2 + 8 * 3"));
	}

	@Test
	void queryWithCharacters() {

		Exception exception = assertThrows(InvalidExpressionException.class, () -> {
			calculatorService.calculateResult("(81 / 3 *34 dfs 4) - 2 * 3 + 4");
		});
		assertTrue(exception instanceof InvalidExpressionException);
	}

	@Test
	void queryWithNoClosingParenthesis() {

		Exception exception = assertThrows(InvalidSyntaxException.class, () -> {
			calculatorService.calculateResult("(81 / 3 * 4) - (2 * 3");
		});
		assertTrue(exception instanceof InvalidSyntaxException);
	}

	@Test
	void queryWithNoOpeningParenthesis() {

		Exception exception = assertThrows(InvalidSyntaxException.class, () -> {
			calculatorService.calculateResult("(81 / 3 * 4)) - (2 * 3)");
		});
		assertTrue(exception instanceof InvalidSyntaxException);
	}

	@Test
	void singleDigit() throws InvalidExpressionException, InvalidSyntaxException {
		assertEquals("10.0", calculatorService.calculateResult("10"));
	}

	@Test
	void queryWithNegation() throws InvalidExpressionException, InvalidSyntaxException {
		assertEquals("108.0", calculatorService.calculateResult("(81 / 3 * 4) - -(2 * 3) + 5"));
	}

	@Test
	void multiplyWithPrenthesis() throws InvalidExpressionException, InvalidSyntaxException {
		assertEquals("97.0", calculatorService.calculateResult("(81 / 3)(4) - (2 * 3) + 5"));
	}

	@Test
	void emptyParenthesis() {
		Exception exception = assertThrows(InvalidSyntaxException.class, () -> {
			calculatorService.calculateResult("()");
		});
		assertTrue(exception instanceof InvalidSyntaxException);
	}
}
