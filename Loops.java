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
                    String code = String.valueOf(statements.extractBlock(block, line));
                    statements.conditionalStatements(code);
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
    // this evaluates condition of while loop
    private boolean evaluateCondition(String conditionExpression) {
        try {
            // Split the condition (e.g., "i <= x")
            String[] parts = conditionExpression.split(" ");
            if (parts.length != 3) {
                System.out.println("Error: Invalid condition format");
                return false;
            }

            Object leftOperand = parts[0].trim(); // e.g., "i"
            String operator = parts[1].trim();   // e.g., "<="
            Object rightOperand = parts[2].trim(); // e.g., "x"

            // Retrieve the values of the operands from the variables map
            Object leftValueObj = interpreter.getValue(leftOperand);
            Object rightValueObj = interpreter.getValue(rightOperand);

            if (leftValueObj == null || rightValueObj == null) {
                System.out.println("Error: Undefined variable in condition");
                return false;
            }

            // Parse the operand values as integers
            int leftValue = Integer.parseInt(leftValueObj.toString());
            int rightValue = Integer.parseInt(rightValueObj.toString());

            // Perform the comparison based on the operator
            return switch (operator) {
                case "<" -> leftValue < rightValue;
                case "<=" -> leftValue <= rightValue;
                case ">" -> leftValue > rightValue;
                case ">=" -> leftValue >= rightValue;
                case "==" -> leftValue == rightValue;
                case "!=" -> leftValue != rightValue;
                default -> {
                    System.out.println("Error: Unsupported operator in condition");
                    yield false;
                }
            };
        } catch (NumberFormatException e) {
            System.out.println("Error: Non-numeric values cannot be compared");
            return false;
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
            return List.of();
        }
        // Collect all lines indented after the current one
        List<String> block = lines.subList(startIndex + 1, lines.size());
        return block.stream()
                .takeWhile(line -> line.startsWith("    ") || line.startsWith("\t"))
                .map(String::trim)
                .toList();
    }
}



