package task_07.ast.expression;

import task_07.Util;
import task_07.compiler.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Call implements Expression {

    private final String functionName;
    private final List<Expression> arguments;


    public Call(String functionName, Expression... arguments) {
        this.functionName = functionName;
        this.arguments = Arrays.asList(arguments);
    }

    public Call(String functionName, List<Expression> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }


    public String getFunctionName() {
        return functionName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }




    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }


    @Override
    public String toString() {
        return Util.f("<Expression.Call fn_name=%s arguments=%s>", functionName, arguments);
    }
}
