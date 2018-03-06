package task_06.ast.expression;

import task_06.compiler.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Call implements Expression {

    private final String functionName;
    private final List<Expression> arguments = new ArrayList<>();


    public Call(String functionName, Expression... arguments) {
        this.functionName = functionName;
        this.arguments.addAll(Arrays.asList(arguments));
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
}
