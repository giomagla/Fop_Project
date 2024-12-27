import java.util.*;

public class ArithmeticInterpreter {

    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        interpreter.getVariables().put("x", 5.0); // Example: Predefined variable x = 5

    }

    public String  computeArithmetics(String expression, Interpreter interpreter) {
        expression = expression.trim(); // Clean up whitespace from the expression
        int result = 0;
        try {
            // Evaluate and print the result
             result = (int) evaluateExpression(expression, interpreter);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return String.valueOf(result);
    }

    public static double evaluateExpression(String expression, Interpreter interpreter) throws Exception {
        //// Remove unnecessary spaces and check for invalid character
        expression = expression.replaceAll("\\s+", "");
        if (!expression.matches("[0-9a-zA-Z+\\-*/%().]*")) {
            throw new Exception("Invalid characters in expression.");
        }

        // Replace variables in the expression with their actual values
        expression = substituteVariables(expression, interpreter);

        // Convert the mathematical expression from infix (standard format) to postfix notation
        List<String> postfix = infixToPostfix(expression);

        //// Calculate and return the final result from the postfix notation
        return evaluatePostfix(postfix);
    }
    // Replace variable names in the expression with their assigned values
    private static String substituteVariables(String expression, Interpreter interpreter) throws Exception {
        StringBuilder substitutedExpression = new StringBuilder(); // Resulting expression
        StringBuilder variableName = new StringBuilder(); // Temporary storage for variable names

        for (int i = 0; i < expression.length(); i++) { // Check if character is part of a variable name
            char c = expression.charAt(i);  // Build variable name

            if (Character.isLetter(c)) {
                variableName.append(c);
            } else {
                if (variableName.length() > 0) {
                    String var = variableName.toString();
                    Object value = interpreter.getValue(var);
                    if (value == null) {
                        throw new Exception("Undefined variable: " + var);
                    }
                    substitutedExpression.append(value);
                    variableName.setLength(0); // Reset the variable name builder
                }
                substitutedExpression.append(c); // Append the current character (operator/number)
            }
        }

        // Handle the case where the last part of the expression is a variable
        if (variableName.length() > 0) {
            String var = variableName.toString();
            Object value = interpreter.getValue(var);
            if (value == null) {
                throw new Exception("Undefined variable: " + var);
            }
            substitutedExpression.append(value);
        }

        return substitutedExpression.toString();
    }
    // Convert an infix expression to postfix notation
    private static List<String> infixToPostfix(String expression) {
        Stack<Character> operators = new Stack<>(); // Stack to hold operators
        List<String> postfix = new ArrayList<>();  // List to store postfix expression
        StringBuilder number = new StringBuilder();  // Temporary storage for numbers

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {  // If character is part of a number
                number.append(c);
            } else {
                if (number.length() > 0) {  // Add the number to the postfix list when a non-digit character is encountered
                    postfix.add(number.toString());
                    number.setLength(0);
                }

                if (c == '(') {
                    operators.push(c);  // Push '(' onto the operator stack
                } else if (c == ')') {
                    // Pop operators until '(' is found
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        postfix.add(String.valueOf(operators.pop()));
                    }
                    operators.pop();  // Remove '('
                } else if (isOperator(c)) {
                    // Pop operators with higher or equal precedence
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                        postfix.add(String.valueOf(operators.pop()));
                    }
                    operators.push(c); // Push the current operator
                }
            }
        }

        // Add any remaining number to the postfix list
        if (number.length() > 0) {
            postfix.add(number.toString());
        }
        // Add remaining operators to the postfix list
        while (!operators.isEmpty()) {
            postfix.add(String.valueOf(operators.pop()));
        }

        return postfix;
    }

    // Evaluate a postfix expression and calculate the result
    private static double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumeric(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                // Pop two operands and apply the operator
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(a, b, token.charAt(0)));
            }
        }

        return stack.pop();
    }

    // Check if a character is a mathematical operator
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    // Determine  which operator has bigger priority
    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
                return 2;
            default:
                return 0;
        }
    }

    // Check if a string represents a numeric value
    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Apply a mathematical operator to two operands
    private static double applyOperator(double a, double b, char operator) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Division by zero is not allowed.");
                }
                return a / b;
            case '%':
                if (b == 0) {
                    throw new ArithmeticException("Modulo by zero is not allowed.");
                }
                return a % b;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}
