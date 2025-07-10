package com.varun.calculator;

import com.varun.calculator.exception.InvalidExpressionException;
import com.varun.calculator.exception.InvalidSyntaxException;
import com.varun.calculator.service.CalculatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CalculatorServiceTests {

    @Autowired
    private CalculatorService calculatorService;

    @Test
    void validQuery() {
        assertEquals(102.0, calculatorService.calculateResult("(81/3*4)-2*3"));
    }

    @Test
    void queryWithWhiteSpaces() {
        assertEquals(82.0, calculatorService.calculateResult("(81 / 3 * 4) - 2 + 8 * 3"));
    }

    @Test
    void queryWithCharacters() {

        Exception exception = assertThrows(InvalidExpressionException.class,
                () -> calculatorService.calculateResult("(81 / 3 *34 dfs 4) - 2 * 3 + 4"));
        assertInstanceOf(InvalidExpressionException.class, exception);
    }

    @Test
    void singleDigit() {
        assertEquals(10.0, calculatorService.calculateResult("10"));
    }

    @Test
    void queryWithNegation() {
        assertEquals(108.0, calculatorService.calculateResult("(81 / 3 * 4) - -(2 * 3) + 5"));
    }

    @Test
    void multiplyWithParenthesis() {
        assertEquals(97.0, calculatorService.calculateResult("(81 / 3)(4) - (2 * 3) + 5"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"()", "(81 / 3 * 4) - (2 * 3", "(81 / 3 * 4)) - (2 * 3)"})
    void queryWithParenthesis(String expression) {

        Exception exception = assertThrows(InvalidSyntaxException.class,
                () -> calculatorService.calculateResult(expression));
        assertInstanceOf(InvalidSyntaxException.class, exception);
    }
}
