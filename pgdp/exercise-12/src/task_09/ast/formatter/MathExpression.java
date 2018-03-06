package task_09.ast.formatter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

// yes, seriously

public class MathExpression {

    private static Object eval(String expression) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");

        try {
            return engine == null ? null : engine.eval(expression);
        } catch (ScriptException e) {
            return null;
        }
    }

    public static boolean producesSameResult(String arg1, String arg2) {
        Object retval_0 = eval(arg1);

        return retval_0 != null && retval_0.equals(eval(arg2));
    }
}
