package com.varun.calculator.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<Map<String, Object>> calculate(@RequestParam(value = "query", required = true) String query) {

		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String expression = new String(Base64.getDecoder().decode(query));

			if (StringUtils.isNotBlank(expression)) {

				// remove the white spaces from the expression 
				double result = calculatorService.calculateResult(expression);
				response.put("error", false);
				response.put("result", result);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

			} else {
				response.put("error", true);
				response.put("message", "Empty Expression");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			}

		} catch (InvalidExpressionException e) {
			e.printStackTrace();
			response.put("error", true);
			response.put("message", "Invalid Expression");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			response.put("error", true);
			response.put("message", "Syntax Error Detected");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", true);
			response.put("message", "Error Occured while processing the request");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
