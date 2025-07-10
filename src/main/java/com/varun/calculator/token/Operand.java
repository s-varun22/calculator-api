package com.varun.calculator.token;

/**
 * @author Varun Srivastava
 */
public record Operand(double value) implements Token {

    public static final Operand ZERO = new Operand(0.0);

    public static Operand parseOperand(String s) {
        return new Operand(Double.parseDouble(s));
    }

    @Override
    public Type getType() {
        return Type.OPERAND;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

}
