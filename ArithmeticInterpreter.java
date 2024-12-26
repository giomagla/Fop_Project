// Interpreter for Minimal Subset of Arithmetic Operations in Java (Supports Operator Precedence)
package project;

import java.util.*;

public class ArithmeticInterpreter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // ask the user to give you arithmetic expression repeatedly
            System.out.print("Enter arithmetic expression (or 'exit' to quit): ");
            String expression = scanner.nextLine().trim();
            // exit the loop if input says exit
            if (expression.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                // Evaluate and print the result
                double result = evaluateExpression(expression);
                System.out.println("Result: " + result);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        //close the scanner
        scanner.close();
    }

    public static double evaluateExpression(String expression) throws Exception {
        // Remove spaces and validate characters
        expression = expression.replaceAll("\\s+", "");
        if (!expression.matches("[0-9+\\-*/%().]*")) {
            throw new Exception("Invalid characters in expression.");
        }

        // Convert infix expression to postfix
        List<String> postfix = infixToPostfix(expression);

        // Evaluate postfix expression
        return evaluatePostfix(postfix);
    }

    //converts the given arithmetic expression to desired structure and calculates its result.
    private static List<String> infixToPostfix(String expression) {
        Stack<Character> operators = new Stack<>();
        List<String> postfix = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        //Iterate through each character in the expression
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            // If the character is part of a number or a decimal point, append it
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else {
                // If a number was being built, add it to the postfix list
                if (number.length() > 0) {
                    postfix.add(number.toString());
                    number.setLength(0); // Reset the number builder
                }

                // Handle parentheses
                if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    // add operators to postfix until the matching '(' is found,so anything that's between parentheses
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        postfix.add(String.valueOf(operators.pop()));
                    }
                    operators.pop(); // Remove '('
                } else if (isOperator(c)) {
                    // checking which operator has more priority to execute it first.
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                        //add it to postfix according to the priorities.
                        postfix.add(String.valueOf(operators.pop()));
                    }
                    operators.push(c); // otherwise save it for later
                }
            }
        }
        // Add any remaining number to the postfix list
        if (number.length() > 0) {
            postfix.add(number.toString());
        }

        // Pop all remaining operators and add them to the postfix list
        while (!operators.isEmpty()) {
            postfix.add(String.valueOf(operators.pop()));
        }

        return postfix;
    }

    //evaluates postfix expression and calculates the result
    private static double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        // Iterate through each token in the postfix expression
        for (String token : postfix) {
            if (isNumeric(token)) {
                // Push numbers onto the stack
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                // Pop two numbers and apply the operator
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(a, b, token.charAt(0)));
            }
        }
        // The remaining item in the stack is the result
        return stack.pop();
    }

    // Checks if the given character is a valid arithmetic operator.
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    // prioritizes given operators
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

    //checks if the given string is numeric.
    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Applies an operator to two numbers and returns the result.
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
