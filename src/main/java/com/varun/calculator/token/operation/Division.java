package com.varun.calculator.token.operation;

import com.varun.calculator.token.Operation;

/**
 * @author Varun Srivastava
 *
 */
public final class Division extends Operation {

    public Division() {
        super('/');
    }

    @Override
    public int getPrecedence() {
        return 1;
    }

    @Override
    public double evaluate(double d1, double d2) {
        return d1 / d2;
    }

}
