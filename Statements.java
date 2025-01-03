import java.util.Scanner;

public class Statements {
    ArithmeticInterpreter ai = new ArithmeticInterpreter();

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
        else if (start > 0 && end > start && end - start <= 2) {
            String value = block.substring(start, end).trim();
            if (interpreter.getVariables().containsKey(value)) {//if the variable exists, get it
                System.out.println(interpreter.getValue(value));
            }
            else {
                System.out.println("Error! provided variable does not exist!");
            }
        }
        else {
            String sb = block.substring(start, end);
            System.out.println(ai.computeArithmetics(sb, interpreter));
        }
    }

    public void conditionalStatements(String code) {
        boolean isIt6 = false;

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

            if (condition.length != 4 && condition.length != 6) {
                System.out.println("Incorrect syntax");
                return;
            }
            String var = "";
            String variableName = "";
            String operator = "";
            String value = "";
            if (condition.length == 6) {
                var = var + condition[1] + condition[2] + condition[3];
                variableName = ai.computeArithmetics(var, interpreter);
                operator = condition[4];
                value = condition[5];
                isIt6 = true;
            } else {
                //if variable exists get it, if not error

                variableName = condition[1];
                operator = condition[2];
                value = condition[3];
                try {
                    interpreter.getValue(variableName);
                } catch (NullPointerException nullPointerException) {
                    System.out.println("Provided variable does not exists");
                }
            }
            if (isConditiontrue(variableName, operator, value, isIt6)) {
                System.out.println("Enter the block of code (end with a single line 'runIf'):");
                StringBuilder block = new StringBuilder();
                while (true) {
                    String line = new Scanner(System.in).nextLine();
                    if (line.equals("runIf")) {
                        break;
                    }
                    block.append(line).append("\n");
                }

                String[] blockLines = block.toString().split("\n");
                for (String blockLine : blockLines) {
                    if (blockLine.startsWith("    print") || blockLine.startsWith("\tprint")) {
                        statements.toBePrinted(blockLine.trim());
                    } else {
                        interpreter.evaluate(blockLine.trim());
                    }
                }
            }

            istrue = isConditiontrue(variableName, operator, value,isIt6);
            //if this is true, when writing else block, it will not exicute because of this boolean
        }
        if (code.startsWith("else:")) {
            if (!code.endsWith(":")) {
                System.out.println("Incorrect syntax, \":\" expected");
                return;
            }
            StringBuilder block = new StringBuilder();
            while (true) {
                String line = new Scanner(System.in).nextLine();
                if (line.equals("runIf")) {
                    break;
                }
                block.append(line).append("\n");
            }

            if (!istrue) {
                String[] blockLines = block.toString().split("\n");
                for (String blockLine : blockLines) {
                    if (blockLine.startsWith("    print") || blockLine.startsWith("\tprint")) {
                        statements.toBePrinted(blockLine.trim());
                    } else {
                        interpreter.evaluate(blockLine.trim());
                    }
                }
            } else {
                System.out.println("error!, \"if\" condition was right");
            }
        }
        else if (code.startsWith("print")) {
            statements.toBePrinted(code);
        } else {
            interpreter.evaluate(code);
        }
        if (code.equals("end"))
            return;
    }

    public boolean isConditiontrue(String variableName, String operator, String value,boolean isIt6) {
        int variablesValue = 0;
        //checking if thing to compare is integer, if not error
        if (!isIt6) {
            try {
                variablesValue = Integer.parseInt((String) interpreter.getValue(variableName));
            } catch (ClassCastException cce) {
                System.out.println("can not compare null to something, " + variableName + " is null");
            }
        }
        else {
            variablesValue = Integer.parseInt(variableName);
        }
        int intValue = 0;
        if(value.matches("-?\\d+")){
            intValue = Integer.parseInt(value);
        }
        else {
            intValue = Integer.parseInt((String) interpreter.getValue(value));
        }
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
    }
}
