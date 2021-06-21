package com.varun.calculator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.varun.calculator.exception.InvalidExpressionException;
import com.varun.calculator.exception.InvalidSyntaxException;
import com.varun.calculator.token.Operand;
import com.varun.calculator.token.Operation;
import com.varun.calculator.token.Parenthesis;
import com.varun.calculator.token.Token;

/**
 * @author Varun Srivastava
 *
 */
@Service
public class CalculatorService {

	private static final Pattern PATTERN = Pattern.compile("([\\d.]+)|([-+*/])|([()])", Pattern.CASE_INSENSITIVE);

	/**
	 * This method is called by the controller to evaluate the mathematical
	 * expression
	 * 
	 * @param expression
	 * @return
	 * @throws InvalidExpressionException
	 * @throws InvalidSyntaxException
	 */
	public String calculateResult(String expression) throws InvalidExpressionException, InvalidSyntaxException {
		return evaluateExpression(validateAndParseExpression(StringUtils.deleteWhitespace(expression))).toString();
	}

	/**
	 * 
	 * The method validates the mathematical expression and creates tokens based on
	 * the Operand, Operation and Parenthesis
	 * 
	 * @param expression
	 * @return list of tokens
	 * @throws InvalidExpressionException
	 */
	private List<Token> validateAndParseExpression(String expression) throws InvalidExpressionException {

		List<Token> tokens = new ArrayList<Token>();

		Matcher matcher = PATTERN.matcher(expression);

		int lastIndex = 0;

		while (matcher.find()) {
			if (matcher.start() != lastIndex) {
				throw new InvalidExpressionException();
			}
			Token token;
			String match;
			if ((match = matcher.group(1)) != null) {
				token = Operand.parseOperand(match);
			} else if ((match = matcher.group(2)) != null) {
				token = Operation.parseOperator(match);
			} else if ((match = matcher.group(3)) != null) {
				token = Parenthesis.parseParenthesis(match);
			} else {
				throw new IllegalStateException();
			}
			tokens.add(token);
			lastIndex = matcher.end();
		}
		return tokens;
	}

	/**
	 * The method evaluates the mathematical expression and checks for syntax errors
	 * 
	 * @param tokens
	 * @return result
	 * @throws InvalidExpressionException
	 * @throws InvalidSyntaxException
	 */
	private Operand evaluateExpression(List<Token> tokens) throws InvalidSyntaxException {
		int start = -1;
		int end = -1;
		int count = 0;

		for (int i = 0; i < tokens.size(); i++) {
			Token s = tokens.get(i);
			if (s.getType() == Token.Type.PARENTHESIS) {
				Parenthesis p = (Parenthesis) s;
				if (p.isOpen()) {
					if (start == -1) {
						start = i;
					} else {
						count++;
					}
				} else {
					if (count > 0) {
						count--;
					} else {
						end = i;
					}
				}

				if (start == -1 && end != -1) { // no open parenthesis present but closing parenthesis present
					throw new InvalidSyntaxException();
				} else if (start != -1 && end != -1) {
					int size = end - start - 1;
					if (size == 0) {
						throw new InvalidSyntaxException();
					}
					List<Token> innerTokens = new ArrayList<Token>(size);
					for (int n = start; n <= end; n++) {
						Token temp = tokens.remove(start);
						if (n != start && n != end) {
							innerTokens.add(temp);
						}
					}
					tokens.add(start, evaluateExpression(innerTokens));
					start = -1;
					end = -1;
					i = start;
				}
			}
		}

		// open parenthesis present but no closing parenthesis
		if (start != -1 && end == -1) {
			throw new InvalidSyntaxException();
		}

		// loop to check if the operands have to be multiplied using parenthesis
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).getType() == Token.Type.OPERAND) {
				tokens.set(i, checkNumbers(i, tokens));
			}
		}

		// loop to calculate the result based on the precedence of the operator
		for (int p = Operation.HIGHEST_PRECEDENCE; p <= Operation.LOWEST_PRECEDENCE; p++) {
			for (int i = tokens.size() - 1; i >= 0; i--) {
				i = checkOperator(i, tokens, p);
			}
			// break the precedence loop if the result is already calculated and no tokens
			// are left in the loop
			if (tokens.size() == 1) {
				break;
			}
		}
		return (Operand) tokens.remove(0);
	}

	/**
	 * This method checks if the numbers are being multiplied with brackets 
	 * 
	 * @param index
	 * @param tokens
	 * @return
	 */
	private Operand checkNumbers(int index, List<Token> tokens) {
		Operand operand = (Operand) tokens.get(index);
		if (index < tokens.size() - 1) {
			int i = index + 1;

			// if number is present after the brackets without any operator then calculate
			// and return the result
			if (tokens.get(i).getType() == Token.Type.OPERAND) {
				Operand result = new Operand(operand.getValue() * checkNumbers(i, tokens).getValue());
				tokens.remove(i);
				return result;
			}
		}
		return operand;
	}

	/**
	 * This method will evaluate the result based on the operator precedence
	 * 
	 * @param index
	 * @param tokens
	 * @param precedence
	 * @return
	 * @throws InvalidSyntaxException
	 */
	private int checkOperator(int index, List<Token> tokens, int precedence) throws InvalidSyntaxException {
		Token token = tokens.get(index);
		if (token.getType() == Token.Type.OPERATION) {
			Operation operator = (Operation) token;
			if (operator.getPrecedence() == precedence) {
				Operand operand = operator.calculateOperationResult(index, tokens);
				tokens.remove(index + 1);
				if (index == 0) {
					tokens.set(index, operand);
				} else {
					tokens.remove(index);
					tokens.set(--index, operand);
				}
			}
		}
		return index;
	}

}
