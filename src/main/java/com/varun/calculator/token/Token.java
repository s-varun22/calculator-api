package com.varun.calculator.token;

/**
 * @author Varun Srivastava
 *
 */
public interface Token {

	public enum Type {
		OPERAND, OPERATION, PARENTHESIS
	}

	public Type getType();

}
