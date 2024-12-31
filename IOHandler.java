import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.*;

public class IOHandler {

    // A map to store variables (like Python does in its environment)
    private final Map<String, Object> variables = new HashMap<>();

    // This method executes the code line by line
    public void execute(String code) {
        String[] lines = code.split("\n"); // Split the code into individual lines
        for (String line : lines) {
            try {
                evaluateLine(line.trim()); // Trim the line and evaluate it
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage()); // Catch any errors and print them
            }
        }
    }

    // This method evaluates each line of code
    private void evaluateLine(String line) throws Exception {
        if (line.startsWith("print(")) {
            handlePrint(line); // Handle print statements
        } else if (line.contains("=")) {
            handleAssignment(line); // Handle variable assignment
        } else {
            throw new Exception("Unsupported statement: " + line); // Throw an error if the statement is unsupported
        }
    }

    // This method handles 'print' statements
    private void handlePrint(String line) throws Exception {
        if (!line.endsWith(")")) {
            throw new Exception("Invalid print statement: " + line);
        }

        String content = line.substring(6, line.length() - 1).trim(); // Extract the content inside print(...)

        if (content.startsWith("'") && content.endsWith("'")) {
            System.out.println(content.substring(1, content.length() - 1)); // Print string literals
        } else if (content.startsWith("f'") && content.endsWith("'")) {
            System.out.println(evaluateFString(content.substring(2, content.length() - 1))); // Evaluate f-string
        } else {
            System.out.println(evaluateExpression(content)); // Evaluate and print the expression
        }
    }

    // This method handles assignment statements
    private void handleAssignment(String line) throws Exception {
        String[] parts = line.split("=", 2);
        if (parts.length != 2) {
            throw new Exception("Invalid assignment statement: " + line);
        }

        String variable = parts[0].trim();
        String expression = parts[1].trim();

        if (!variable.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            throw new Exception("Invalid variable name: " + variable);
        }

        if (expression.startsWith("input(")) {
            handleInput(expression, variable); // Handle input with the variable name
        } else {
            Object value = evaluateExpression(expression);
            variables.put(variable, value); // Assign the evaluated expression to the variable
        }
    }

    // This method handles 'input' statements
    private void handleInput(String line, String variableName) throws Exception {
        if (!line.endsWith(")")) {
            throw new Exception("Invalid input statement: " + line);
        }

        String prompt = line.substring(6, line.length() - 1).trim();

        if (prompt.startsWith("'") && prompt.endsWith("'")) {
            System.out.print(prompt.substring(1, prompt.length() - 1));
        } else {
            throw new Exception("Invalid prompt in input statement: " + prompt);
        }

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine(); // Read user input

        variables.put(variableName, input); // Store the input in the specified variable
    }

    // This method evaluates an expression
    private Object evaluateExpression(String expression) throws Exception {
        if (variables.containsKey(expression)) {
            return variables.get(expression); // Return variable value
        }

        try {
            if (expression.contains(".")) {
                return Double.parseDouble(expression); // Parse as double
            } else {
                return Integer.parseInt(expression); // Parse as integer
            }
        } catch (NumberFormatException ignored) {}

        if (expression.startsWith("'") && expression.endsWith("'")) {
            return expression.substring(1, expression.length() - 1); // Return string literal
        }

        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            double sum = 0;
            for (String part : parts) {
                sum += Double.parseDouble(evaluateExpression(part.trim()).toString());
            }
            return sum; // Return the sum of the parts
        }

        throw new Exception("Unsupported expression: " + expression);
    }

    // This method evaluates f-strings
    private String evaluateFString(String content) throws Exception {
        StringBuilder result = new StringBuilder();
        StringBuilder variableBuffer = new StringBuilder();
        boolean insideVariable = false;

        for (char c : content.toCharArray()) {
            if (insideVariable) {
                if (c == '}') {
                    String variableName = variableBuffer.toString();
                    Object value = variables.get(variableName);
                    if (value == null) {
                        throw new Exception("Undefined variable in f-string: " + variableName);
                    }
                    result.append(value);
                    insideVariable = false;
                    variableBuffer.setLength(0);
                } else {
                    variableBuffer.append(c);
                }
            } else {
                if (c == '{') {
                    insideVariable = true;
                } else {
                    result.append(c);
                }
            }
        }

        if (insideVariable) {
            throw new Exception("Unclosed variable in f-string");
        }

        return result.toString();
    }
}
