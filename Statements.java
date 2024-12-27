import java.util.Scanner;

public class Statements {

    private final Interpreter interpreter;


    public Statements(Interpreter interpreter) {
        this.interpreter = interpreter;
    }
    //this is function print(...)
    public void toBePrinted(String block) {
        int start = block.indexOf("(") + 1;
        int end = block.indexOf(")");
        //checking if it is string
        if (block.charAt(start) == '"' && block.charAt(end - 1) == '"') {
            String toBePrinted = block.substring(start + 1, end - 1);
            System.out.println(toBePrinted);
        }
        //checking if it is variable
        else if (start > 0 && end > start) {
            String value = block.substring(start, end).trim();
            if (interpreter.getVariables().containsKey(value)) {//if the variable exists, get it
                System.out.println(interpreter.getValue(value));
            }
            else {
                System.out.println("Error! provided variable does not exist!");
            }
        }
    }

    public void conditionalStatements(String code) {
        boolean istrue = false;
        Statements statements = new Statements(interpreter);
        //check if provided code is if statement
        if (code.startsWith("if")) {
            code.substring(2).trim();
            if (!code.endsWith(":")) {
                System.out.println("Incorrect syntax,\":\" expected");
                return;
            }
            code = code.substring(0, code.length() - 1).trim();
            String[] condition = code.split(" ");

            if (condition.length != 4) {
                System.out.println("Incorrect syntax");
                return;
            }
            String variableName = condition[1];
            //if variable exists get it, if not error
            try {
                interpreter.getValue(variableName);
            } catch (NullPointerException nullPointerException) {
                System.out.println("Provided variable does not exists");
            }
            String operator = condition[2];
            String value = condition[3];
            if (isConditiontrue(variableName, operator, value)) {
                while (true) {
                    Scanner scanner = new Scanner(System.in);
                    String block = scanner.nextLine();
                    if (!block.startsWith("    ") && !block.startsWith("\t")) {//must be space sensitively,
                        //if not, exit if block and notify it
                        if (block.startsWith("print")) {
                            System.out.println("You are out of the if block");
                            statements.toBePrinted(block);
                        } else if (block.startsWith("if")) {
                            System.out.println("You are out of the if block");
                            conditionalStatements(block);
                        } else if (block.startsWith("else:")) {
                            code = block;
                            System.out.println("You are out of the if block");
                        } else
                            System.out.println("You are out of the if block");
                        interpreter.evaluate(block);
                        break;
                    }
                    if (block.startsWith("    print") || block.startsWith("\tprint")) {
                        statements.toBePrinted(block);
                    } else {
                        interpreter.evaluate(block);
                    }
                }
            }
            istrue = isConditiontrue(variableName, operator, value);
            //if this is true, when writing else block, it will not exicute because of this boolean
        }
        if (code.startsWith("else:")) {
            if (!code.endsWith(":")) {
                System.out.println("Incorrect syntax,\":\" expected");
                return;
            }
            Scanner scanner = new Scanner(System.in);
            String elseCode = scanner.nextLine();
            if (!istrue) {
                if (elseCode.startsWith("    print") || elseCode.startsWith("\tprint")) {
                    statements.toBePrinted(elseCode);
                } else {
                    interpreter.evaluate(elseCode);
                }
            } else if (istrue) {
                //right here
                System.out.println("error!, \"if\" condition was right");
            }
        } else if (code.startsWith("print")) {
            statements.toBePrinted(code);
        } else {
            interpreter.evaluate(code);
        }
        if (code.equals("end"))
            return;
    }

    public boolean isConditiontrue(String variableName, String operator, String value) {
        int variablesValue = 0;
        //checking if thing to compare is integer, if not error
        try {
            variablesValue = (int) interpreter.getValue(variableName);
        } catch (NullPointerException npe) {
            System.out.println("can not compare null to something, " + variableName + " is null");
        }
        int intValue = Integer.parseInt(value);
        switch (operator) {
            //checking all operators that are legal
            case "==":
                return variablesValue == intValue;
            case "!=":
                return variablesValue != intValue;
            case "<":
                return variablesValue < intValue;
            case ">":
                return variablesValue > intValue;
            case "<=":
                return variablesValue <= intValue;
            case ">=":
                return variablesValue >= intValue;
            default:
                System.out.println("Error: Unsupported operator " + operator);
                return false;
        }
