package com.varun.calculator.token.operation;

import com.varun.calculator.token.Operation;

/**
 * @author Varun Srivastava
 *
 */
public final class Subtraction extends Operation {

    public Subtraction() {
        super('-');
    }

    @Override
    public int getPrecedence() {
        return 4;
    }

    @Override
    public double evaluate(double d1, double d2) {
        return d1 - d2;
    }

}
