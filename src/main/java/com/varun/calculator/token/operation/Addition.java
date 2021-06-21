package com.varun.calculator.token.operation;

import com.varun.calculator.token.Operation;

/**
 * @author Varun Srivastava
 *
 */
public final class Addition extends Operation {

    public Addition() {
        super('+');
    }

    @Override
    public int getPrecedence() {
        return 3;
    }

    @Override
    public double evaluate(double d1, double d2) {
        return d1 + d2;
    }
}
