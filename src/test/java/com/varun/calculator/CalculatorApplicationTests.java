package com.varun.calculator;

import com.varun.calculator.controller.CalculatorController;
import com.varun.calculator.exception.GlobalExceptionHandler;
import com.varun.calculator.exception.InvalidExpressionException;
import com.varun.calculator.exception.InvalidSyntaxException;
import com.varun.calculator.service.CalculatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CalculatorController.class)
@Import(GlobalExceptionHandler.class)
class CalculatorApplicationTests {

    @MockitoBean
    CalculatorService calculatorService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validResponse() throws Exception {

        given(calculatorService.calculateResult("MiAqICgyMy8oMyozKSktIDIzICogKDIqMyk")).willReturn(anyDouble());

        mockMvc.perform(get("/calculus?query=" + "MiAqICgyMy8oMyozKSktIDIzICogKDIqMyk"))
                .andExpect(status().isOk());
    }

    @Test
    void emptyQuery() throws Exception {

        mockMvc.perform(get("/calculus?query=" + "IA"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(InvalidExpressionException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Empty Expression",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void syntaxErrorQuery() throws Exception {

        given(calculatorService.calculateResult(anyString()))
                .willThrow(new InvalidSyntaxException("Syntax Error Detected"));

        mockMvc.perform(get("/calculus?query=" + "KDgxIC8gMyAqMyAqKiA0KSAtIDIgKiAzICsgNA"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(InvalidSyntaxException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Syntax Error Detected",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void invalidExpressionException() throws Exception {

        given(calculatorService.calculateResult(anyString()))
                .willThrow(new InvalidExpressionException("Invalid Expression"));

        mockMvc.perform(get("/calculus?query=" + "KDgxIC8gMyAqMzQgZGZzIDQpIC0gMiAqIDMgKyA0"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(InvalidExpressionException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Invalid Expression",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void decodeErrorHandler() throws Exception {

        given(calculatorService.calculateResult(anyString()))
                .willThrow(new RuntimeException("Error Occurred while processing the request"));

        mockMvc.perform(get("/calculus?query=" + "KDgxIC8gMyAqMzQgZGZzIDQpIC0gMiAqIDMgKyA0"))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> assertInstanceOf(Exception.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Error Occurred while processing the request",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}
