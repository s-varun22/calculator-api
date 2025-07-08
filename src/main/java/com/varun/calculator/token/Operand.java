package com.varun.calculator.token;

/**
 * @author Varun Srivastava
 *
 */
public final class Operand implements Token {

    public static final Operand ZERO = new Operand(0.0);

    private final double value;

    public Operand(double value) {
        this.value = value;
    }

    public static Operand parseOperand(String s) {
        return new Operand(Double.parseDouble(s));
    }

    public double getValue() {
        return value;
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
