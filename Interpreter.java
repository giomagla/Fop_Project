import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    private final Map<String, Object> variables = new HashMap<>();

    public Map<String, Object> getVariables() {
        return variables;
    }

    public Object getValue(Object i) {
        return getVariables().get(i);
    }

    public void evaluate(String code) {

        String[] parts = code.split("=");
        if (parts.length == 2) {
            String variable = parts[0].trim();
            Object value = evaluateExpression(parts[1].trim());
            variables.put(variable, value);
            System.out.println("Assigned: " + variable + " = " + value);
        }
    }


    private Object evaluateExpression(String expression) {
        try {
            return Integer.parseInt(expression);
        } catch (NumberFormatException ignored) {
        }
        try {
            return Double.parseDouble(expression);
        } catch (NumberFormatException ignored) {
        }
        char[] chars = expression.toCharArray();
        if (chars[0] == '\'' && chars[chars.length - 1] == '\'') {
            if (chars[1] != '\'') {
                return chars[1];
            }
            System.out.println("empty literal character");
            return null;
        }
        if (chars[0] != '"' || chars[chars.length - 1] != '"') {
            if (!(expression.equals("true")) && !(expression.equals("false")))
                return null;
            else if (expression.equals("true") || expression.equals("false")) {
                return Boolean.parseBoolean(expression);
            }
            return null;
        }

        int i = 1;
        StringBuilder stringBuilder = new StringBuilder();
        while (i < expression.length() - 1) {
            stringBuilder.append(chars[i]);
            i++;
        }
        String newExp = stringBuilder.toString();
        return newExp;
    }
}

