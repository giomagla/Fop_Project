import java.util.List;

public class Loops {
    private final Interpreter interpreter;
    private final ArithmeticInterpreter arithmeticInterpreter;
    private final Statements statements;

    public Loops(Interpreter interpreter, ArithmeticInterpreter arithmeticInterpreter, Statements statements) {
        this.interpreter = interpreter;
        this.arithmeticInterpreter = arithmeticInterpreter;
        this.statements = statements;
    }

    public void handleWhileLoop(String condition, List<String> block) {
        // Validate and execute the condition
        if (!condition.startsWith("while") || !condition.endsWith(":")) {
            System.out.println("Error: Invalid while loop syntax.");
            return;
        }

        // separate condition from "while" statement
        String conditionExpression = condition.substring(5, condition.length() - 1).trim();

        // Execute the while loop
        while (evaluateCondition(conditionExpression)) {
            for (String line : block) {
                line = line.trim();

                // Process nested if statements
                if (line.startsWith("if")) {
                    statements.conditionalStatements(line, extractBlock(block, line));
                }
                // Handle variable assignments
                else if (line.contains("=")) {
                    interpreter.evaluate(line);
                }
                // Handle print statements
                else if (line.startsWith("print")) {
                    statements.toBePrinted(line);
                }
                // Handle arithmetic expressions
                else if (isArithmeticExpression(line)) {
                    String result = arithmeticInterpreter.computeArithmetics(line, interpreter);
                    System.out.println("Arithmetic Result: " + result);
                }
           else {
                    System.out.println("Error: Unsupported statement in while loop.");
                }
            }
        }
    }

    private boolean evaluateCondition(String conditionExpression) {
        try {
            // Split the condition into 3 parts ( x, <, 3)
            String[] parts = conditionExpression.split(" ");
            if (parts.length != 3) {
                System.out.println("Error: Invalid condition syntax");
                return false;
            }

            // Extract the variable, operator, and value
            String variable = parts[0].trim();
            String operator = parts[1].trim();
            String value = parts[2].trim();

            // Get the current value of the variable
            Object variableValueObj = interpreter.getValue(variable);

            if (variableValueObj == null) {
                System.out.println("Error: Variable '" + variable + "' does not exist or is null");
                return false;
            }

            // Convert variable value and target value to integers (if applicable)
            int variableValue;
            int targetValue;

            try {
                variableValue = Integer.parseInt(variableValueObj.toString());
                targetValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Error: Non-numeric values cannot be compared");
                return false;
            }

            // Evaluate the condition
            // replases enhanced switch
            return switch (operator) {
                case "<" -> variableValue < targetValue;
                case "<=" -> variableValue <= targetValue;
                case ">" -> variableValue > targetValue;
                case ">=" -> variableValue >= targetValue;
                case "==" -> variableValue == targetValue;
                case "!=" -> variableValue != targetValue;
                default -> {
                    System.out.println("Error: Unsupported operator '" + operator + "'");
                    yield false;
                }
            };
        } catch (Exception e) {
            System.out.println("Error evaluating condition: " + e.getMessage());
            return false;
        }
    }


    private boolean isArithmeticExpression(String line) {
        try {
            arithmeticInterpreter.computeArithmetics(line, interpreter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<String> extractBlock(List<String> lines, String start) {
        int startIndex = lines.indexOf(start);
        if (startIndex == -1) {
            return List.of(); // Return an empty list if the block cannot be found
        }

        // Collect all lines indented after the current one
        List<String> block = lines.subList(startIndex + 1, lines.size());
        return block.stream()
                .takeWhile(line -> line.startsWith("    ") || line.startsWith("\t"))
                .map(String::trim)
                .toList();
    }
}



