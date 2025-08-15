package com.varun.calculator;

import com.varun.calculator.exception.InvalidExpressionException;
import com.varun.calculator.exception.InvalidSyntaxException;
import com.varun.calculator.service.CalculatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
        assertEquals(130.0, calculatorService.calculateResult("(81 / 3 * 4) - 2 + 8 * 3"));
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
        assertEquals(119.0, calculatorService.calculateResult("(81 / 3 * 4) - -(2 * 3) + 5"));
    }

    @Test
    void decimalNumbers() {
        assertEquals(7.0, calculatorService.calculateResult("3.5*2"));
        assertEquals(2.0, calculatorService.calculateResult("12/(2*(2+1))"));
    }

    @Test
    void doubleUnaryMinusAtStart() {
        assertEquals(5.0, calculatorService.calculateResult("--5"));
    }

    @ParameterizedTest
    @CsvSource({
            "--5, 5.0",
            "----5, 5.0",
            "-+-+5, 5.0",
            "+-+5, -5.0"
    })
    void chainedUnaryAtStart(String expr, double expected) {
        assertEquals(expected, calculatorService.calculateResult(expr));
    }

    @Test
    void unaryInsideExpression() {
        assertEquals(15.0, calculatorService.calculateResult("3*--5"));
        assertEquals(-2.0, calculatorService.calculateResult("10/+-5"));
    }

    @ParameterizedTest
    @CsvSource({
            "--(2+3), 5.0",
            "+-(2+3), -5.0"
    })
    void chainedUnaryBeforeParentheses(String expr, double expected) {
        assertEquals(expected, calculatorService.calculateResult(expr));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-", "--", "+)", "-*"})
    void invalidUnaryWithoutOperand(String expr) {
        assertThrows(InvalidSyntaxException.class, () -> calculatorService.calculateResult(expr));
    }

    @Test
    void unaryWithDecimals() {
        assertEquals(-0.3, calculatorService.calculateResult("-0.5+0.2"), 1e-9);
    }

    @Test
    void doubleUnaryPlusAtStart() {
        assertEquals(5.0, calculatorService.calculateResult("++5"));
    }

    @Test
    void unaryMinusOnParentheses() {
        assertEquals(-5.0, calculatorService.calculateResult("-(2+3)"));
    }

    @Test
    void chainedImplicitMultiplication() {
        assertEquals(24.0, calculatorService.calculateResult("2(3)(4)"));
        assertEquals(33.0, calculatorService.calculateResult("(1+2)3+4(5+1)"));
    }

    @Test
    void operatorAtEndThrows() {
        assertThrows(RuntimeException.class, () -> calculatorService.calculateResult("2+"));
    }

    @Test
    void spacesOnlyExpressionThrows() {
        assertThrows(InvalidExpressionException.class, () -> calculatorService.calculateResult("   "));
    }

    @Test
    void multiplyWithParenthesis() {
        assertEquals(107.0, calculatorService.calculateResult("(81 / 3)(4) - (2 * 3) + 5"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"()", "(81 / 3 * 4) - (2 * 3", "(81 / 3 * 4)) - (2 * 3)"})
    void queryWithParenthesis(String expression) {

        Exception exception = assertThrows(InvalidSyntaxException.class,
                () -> calculatorService.calculateResult(expression));
        assertInstanceOf(InvalidSyntaxException.class, exception);
    }
}
