package com.varun.calculator.controller;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.varun.calculator.dto.Response;
import com.varun.calculator.exception.InvalidExpressionException;
import com.varun.calculator.exception.InvalidSyntaxException;
import com.varun.calculator.service.CalculatorService;

/**
 * @author Varun Srivastava
 *
 */
@RestController
public class CalculatorController {

	@Autowired
	CalculatorService calculatorService;

	@GetMapping("/calculus")
	public ResponseEntity<Response> calculate(@RequestParam(value = "query", required = true) String query) {

		Response response = new Response();

		try {
			String expression = new String(Base64.getDecoder().decode(query));

			if (StringUtils.isNotBlank(expression)) {

				// remove the white spaces from the expression 
				response.setResult(calculatorService.calculateResult(expression));

				return new ResponseEntity<Response>(response, HttpStatus.OK);

			} else {
				response.setError("Empty Expression");
				return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
			}

		} catch (InvalidExpressionException e) {
			e.printStackTrace();
			response.setError("Invalid Expression");
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			response.setError("Syntax Error Detected");
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError("Error Occured while processing the request");
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
