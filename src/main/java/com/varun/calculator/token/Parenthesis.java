package com.varun.calculator.token;

/**
 * @author Varun Srivastava
 *
 */
public final class Parenthesis implements Token {

	private final boolean open;

	private Parenthesis(boolean open) {
		this.open = open;
	}

	public static Parenthesis parseParenthesis(String s) {
		if (s.length() == 1) {
			char symbol = s.charAt(0);
			if (symbol == '(') {
				return new Parenthesis(true);
			}
			return new Parenthesis(false);
		}
		return null;
	}

	public boolean isOpen() {
		return open;
	}

	@Override
	public Type getType() {
		return Type.PARENTHESIS;
	}

	@Override
	public String toString() {
		return this.open ? "(" : ")";
	}
}
