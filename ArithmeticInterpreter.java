// Interpreter for Minimal Subset of Arithmetic Operations in Java (Supports Operator Precedence)
package project;

import java.util.*;

public class ArithmeticInterpreter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter arithmetic expression (or 'exit' to quit): ");
            String expression = scanner.nextLine().trim();

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

    private static List<String> infixToPostfix(String expression) {
        Stack<Character> operators = new Stack<>();
        List<String> postfix = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else {
                if (number.length() > 0) {
                    postfix.add(number.toString());
                    number.setLength(0);
                }

                if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        postfix.add(String.valueOf(operators.pop()));
                    }
                    operators.pop(); // Remove '('
                } else if (isOperator(c)) {
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                        postfix.add(String.valueOf(operators.pop()));
                    }
                    operators.push(c);
                }
            }
        }

        if (number.length() > 0) {
            postfix.add(number.toString());
        }

        while (!operators.isEmpty()) {
            postfix.add(String.valueOf(operators.pop()));
        }

        return postfix;
    }

    private static double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumeric(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(a, b, token.charAt(0)));
            }
        }

        return stack.pop();
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

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

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

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
