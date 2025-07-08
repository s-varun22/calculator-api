package com.varun.calculator.token;

import java.util.List;

import com.varun.calculator.exception.InvalidSyntaxException;
import com.varun.calculator.token.operation.Addition;
import com.varun.calculator.token.operation.Division;
import com.varun.calculator.token.operation.Multiplication;
import com.varun.calculator.token.operation.Subtraction;

/**
 * @author Varun Srivastava
 *
 */
public abstract class Operation implements Token {

	public static final int HIGHEST_PRECEDENCE = 1;
	public static final int LOWEST_PRECEDENCE = 4;

	public static final Addition ADDITION = new Addition();
	public static final Subtraction SUBTRACTION = new Subtraction();
	public static final Multiplication MULTIPLICATION = new Multiplication();
	public static final Division DIVISION = new Division();

	public static final Operation[] OPERATORS = new Operation[] { ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION };

	public final char symbol;

	protected Operation(char symbol) {
		this.symbol = symbol;
	}

	public static Operation parseOperator(String s) {
		if (s.length() == 1) {
			for (Operation operator : OPERATORS) {
				if (operator.symbol == s.charAt(0)) {
					return operator;
				}
			}
		}
		return null;
	}

	public abstract int getPrecedence();

	/**
	 * This method is implemented in the child classes to performs respective
	 * operations based on the operator
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	protected abstract double evaluate(double d1, double d2);

	/**
	 * The methods performs the operation and returns the result
	 * 
	 * @param index
	 * @param tokens
	 * @return
	 * @throws InvalidSyntaxException
	 */
	public Operand calculateOperationResult(int index, List<Token> tokens) throws InvalidSyntaxException {

		final Operand operand1;
		if (symbol == '+' || symbol == '-') {
			Token before;
			if (index > 0 && (before = tokens.get(index - 1)).getType() != Token.Type.OPERATION) {
				operand1 = (Operand) before;
			} else {
				// change the sign of the operand
				operand1 = null;
			}
		} else {
			if (index > 0) {
				Token before = tokens.get(index - 1);
				if (before.getType() == Token.Type.OPERAND) {
					operand1 = (Operand) before;
				} else {
					throw new InvalidSyntaxException();
				}
			} else {
				throw new InvalidSyntaxException();
			}
		}

		final Operand operand2;
		if (index < tokens.size() - 1) {
			int i = index + 1;
			Token after = tokens.get(i);
			if (after.getType() == Token.Type.OPERATION) {
				Operation operation = (Operation) after;
				if (operation.symbol == '+' || operation.symbol == '-') {
					operand2 = operation.calculateOperationResult(i, tokens);
					tokens.remove(i);
				} else {
					throw new InvalidSyntaxException();
				}
			} else {
				operand2 = (Operand) after;
			}
		} else {
			// No number present after the operator
			throw new InvalidSyntaxException();
		}
		double d1 = operand1 != null ? operand1.getValue() : 0.0;
		double d2 = operand2.getValue();
		return new Operand(evaluate(d1, d2));
	}

	@Override
	public final Type getType() {
		return Type.OPERATION;
	}

	@Override
	public String toString() {
		return Character.toString(symbol);
	}
}
