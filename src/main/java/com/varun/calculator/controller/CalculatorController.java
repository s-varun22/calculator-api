package com.varun.calculator.controller;

import com.varun.calculator.controller.dto.ResponseDto;
import com.varun.calculator.exception.InvalidExpressionException;
import com.varun.calculator.service.CalculatorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Varun Srivastava
 */
@RestController
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/calculus")
    public ResponseEntity<ResponseDto> calculate(@RequestParam(value = "query") String query) {
        final String expression;
        try {
            byte[] bytes = Base64.getDecoder().decode(query);
            expression = new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new InvalidExpressionException("Query must be Base64-encoded UTF-8 text");
        }

        if (StringUtils.isBlank(expression)) {
            throw new InvalidExpressionException("Empty Expression");
        }

        double result = calculatorService.calculateResult(expression);
        return new ResponseEntity<>(new ResponseDto(result), HttpStatus.OK);
    }

}