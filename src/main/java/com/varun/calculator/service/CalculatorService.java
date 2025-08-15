package com.varun.calculator.service;

import com.varun.calculator.exception.InvalidExpressionException;
import com.varun.calculator.exception.InvalidSyntaxException;
import com.varun.calculator.token.Operand;
import com.varun.calculator.token.Operation;
import com.varun.calculator.token.Parenthesis;
import com.varun.calculator.token.Token;
import com.varun.calculator.token.operation.Addition;
import com.varun.calculator.token.operation.Subtraction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Varun Srivastava
 */
@Service
public class CalculatorService {

    private static final Pattern PATTERN = Pattern.compile("([\\d.]+)|([-+*/])|([()])", Pattern.CASE_INSENSITIVE);

    /**
     * This method is called by the controller to evaluate the mathematical
     * expression
     *
     * @param expression the mathematical expression to be evaluated
     * @return the result of the mathematical expression
     */
    public double calculateResult(String expression) {
        String trimmed = StringUtils.deleteWhitespace(expression);
        if (trimmed.isEmpty()) {
            throw new InvalidExpressionException("Empty expression");
        }
        return evaluateExpression(validateAndParseExpression(trimmed)).value();
    }

    /**
     * The method validates the mathematical expression and creates tokens based on
     * the Operand, Operation and Parenthesis
     *
     * @param expression the mathematical expression to be evaluated
     * @return list of tokens
     */
    private List<Token> validateAndParseExpression(String expression) {

        List<Token> tokens = new ArrayList<>();

        Matcher matcher = PATTERN.matcher(expression);

        int lastIndex = 0;

        while (matcher.find()) {
            if (matcher.start() != lastIndex) {
                throw new InvalidExpressionException("Invalid Expression");
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
        if (lastIndex != expression.length()) {
            throw new InvalidExpressionException("Invalid Expression");
        }
        return tokens;
    }

    /**
     * The method evaluates the mathematical expression and checks for syntax errors
     *
     * @param tokens list of tokens representing the mathematical expression
     * @return Operand representing the result of the evaluated expression
     * @throws InvalidExpressionException if the expression is invalid
     * @throws InvalidSyntaxException     if there is a syntax error in the expression
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
                    throw new InvalidSyntaxException("Syntax Error Detected");
                } else if (start != -1 && end != -1) {
                    int size = end - start - 1;
                    if (size == 0) {
                        throw new InvalidSyntaxException("Syntax Error Detected");
                    }
                    List<Token> innerTokens = new ArrayList<>(size);
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
            throw new InvalidSyntaxException("Syntax Error Detected");
        }

        // normalize unary plus/minus into signed operands
        normalizeUnarySigns(tokens);

        // process the multiplication using parenthesis
        processOperandMultiplication(tokens);

        // calculate the result based on precedence of the operator
        processOperatorsByPrecedence(tokens);

        return (Operand) tokens.removeFirst();
    }

    /**
     * Processes tokens to handle implicit multiplication between operands and brackets.
     *
     * @param tokens the list of tokens to check and potentially update
     */
    private void processOperandMultiplication(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == Token.Type.OPERAND) {
                tokens.set(i, checkNumbers(i, tokens));
            }
        }
    }

    /**
     * Processes the operators to the tokens list based on precedence, until only one token (the result) remains.
     *
     * @param tokens the list of tokens to process
     */
    private void processOperatorsByPrecedence(List<Token> tokens) {
        for (int p = Operation.HIGHEST_PRECEDENCE; p <= Operation.LOWEST_PRECEDENCE; p++) {
            int i = 0;
            while (i < tokens.size()) {
                int updatedIndex = checkOperator(i, tokens, p);
                i = (updatedIndex == i) ? i + 1 : updatedIndex;
            }
            // break the loop if the result is already calculated and no tokens are left
            if (tokens.size() == 1) {
                break;
            }
        }
    }

    /**
     * Converts unary "+"/"-" operators into signed operands.
     *
     * @param tokens the list of tokens representing the expression
     * @throws InvalidSyntaxException if the syntax is invalid for unary operators
     */
    private void normalizeUnarySigns(List<Token> tokens) throws InvalidSyntaxException {

        ListIterator<Token> it = tokens.listIterator();

        Token prev = null; // to track the previous token to determine unary context

        while (it.hasNext()) {
            Token current = it.next();
            boolean isCurrentOperation = isOperation(current);
            boolean unaryContext = isCurrentOperation && (prev == null || isOperation(prev));

            if (!unaryContext) {
                // Not a unary operator; advance and remember this token as previous
                prev = current;
                continue; // the single continue used in this loop
            }

            // Collapse a run of leading unary +/-, then apply the net sign to the next operand
            boolean negative = false;

            // Remove the current unary operator and record sign if it's '-'
            Operation op = (Operation) current;
            if (op instanceof Subtraction) {
                negative = true;
            }
            it.remove();

            // Consume subsequent unary operators in sequence (e.g., --5, +--+5)
            while (it.hasNext()) {
                Token nextTok = tokens.get(it.nextIndex());
                if (nextTok.getType() != Token.Type.OPERATION) {
                    break;
                }
                Operation nextOp = (Operation) nextTok;
                if (nextOp instanceof Addition) {
                    it.next();
                    it.remove(); // drop unary '+'
                } else if (nextOp instanceof Subtraction) {
                    it.next();
                    it.remove(); // drop unary '-' and flip sign
                    negative = !negative;
                } else {
                    break;
                }
            }

            // After collapsing the run, the next token must be an operand
            if (!it.hasNext() || tokens.get(it.nextIndex()).getType() != Token.Type.OPERAND) {
                throw new InvalidSyntaxException("Syntax Error Detected");
            }

            // If the net sign is negative, negate the operand in place
            if (negative) {
                int idx = it.nextIndex();
                Operand next = (Operand) tokens.get(idx);
                tokens.set(idx, new Operand(-next.value()));
            }
        }
    }

    /**
     * Checks if the given token is an operation.
     *
     * @param token the token to check
     * @return true if the token is of type OPERATION, false otherwise
     */
    private boolean isOperation(Token token) {
        return token.getType() == Token.Type.OPERATION;
    }

    /**
     * This method checks if the numbers are being multiplied with brackets
     *
     * @param index  the current index of the operand token in the list
     * @param tokens the list of tokens representing the expression
     * @return an Operand representing the multiplication result if applicable, or the original operand
     */
    private Operand checkNumbers(int index, List<Token> tokens) {
        Operand operand = (Operand) tokens.get(index);
        if (index < tokens.size() - 1) {
            int i = index + 1;

            // if number is present after the brackets without any operator then calculate
            // and return the result
            if (tokens.get(i).getType() == Token.Type.OPERAND) {
                Operand result = new Operand(operand.value() * checkNumbers(i, tokens).value());
                tokens.remove(i);
                return result;
            }
        }
        return operand;
    }

    /**
     * This method checks an operator at the given index if its precedence matches the target precedence.
     *
     * @param index      the position of the token to check and potentially process
     * @param tokens     the list of tokens representing the expression
     * @param precedence the current operator precedence level being processed
     * @return the updated index after processing to continue iteration correctly
     */
    private int checkOperator(int index, List<Token> tokens, int precedence) {
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
