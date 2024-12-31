import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        Statements statements = new Statements(interpreter);
        ArithmeticInterpreter arithmeticInterpreter = new ArithmeticInterpreter();
        Loops loops = new Loops(interpreter, arithmeticInterpreter, statements);
        Scanner scanner = new Scanner(System.in);

        List<String> codeLines = new ArrayList<>();
        System.out.println("Enter your code. Type 'run' to execute or 'exit' to quit.");

        while (true) {
            String line = scanner.nextLine();
            // The code only runs after I type 'run'
            if (line.equalsIgnoreCase("run")) {
                executeCode(codeLines, statements, loops, interpreter);
                codeLines.clear(); // Clear the code lines for the next input
                // After I run, type exit to halt the program
            } else if (line.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the program.");
                break;
            } else {
                codeLines.add(line);
            }
        }
    }

    private static void executeCode(List<String> codeLines, Statements statements, Loops loops, Interpreter interpreter) {
        int i = 0;

        while (i < codeLines.size()) {
            String line = codeLines.get(i);

            if (line.startsWith("while")) {
                // This creates a block of code that is inside the while loop and takes it all to the loops class
                List<String> block = new ArrayList<>();
                i++; // Move to the block's first line
                while (i < codeLines.size() && (codeLines.get(i).startsWith("    ") || codeLines.get(i).startsWith("\t"))) {
                    block.add(codeLines.get(i).trim());
                    i++;
                }
                loops.handleWhileLoop(line, block); // Pass the while condition and block to the Loops class
            } else if (line.startsWith("if")) {
                // Same thing with if
                List<String> block = new ArrayList<>();
                i++; // Move to the block's first line
                while (i < codeLines.size() && (codeLines.get(i).startsWith("    ") || codeLines.get(i).startsWith("\t"))) {
                    block.add(codeLines.get(i).trim());
                    i++;
                }
                statements.conditionalStatements(String.valueOf(block)); // Pass the if condition and block to the Statements class
            } else if (line.startsWith("print")) {
                // Prints
                statements.toBePrinted(line);
                i++;
            } else {
                // Evaluate a single line, assignments or arithmetic operations
                interpreter.evaluate(line);
                i++;
            }
        }
    }
}
