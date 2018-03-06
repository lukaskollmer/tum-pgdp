package task_07.ast.expression;

import task_07.Util;
import task_07.ast.Operator;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static task_07.Util.f;

public class Call implements Expression, Operator {

    public final String functionName;
    public final List<Expression> arguments;


    public Call(String functionName, Expression... arguments) {
        this.functionName = functionName;
        this.arguments = Arrays.asList(arguments);
    }

    public Call(String functionName, List<Expression> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }



    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }

    @Override
    public void accept(FormatVisitor formatVisitor) {
        formatVisitor.visit(this);
    }


    @Override
    public String toString() {
        return f("<Expression.Call fn_name=%s arguments=%s>", functionName, arguments);
    }

    @Override
    public int precedence() {
        return 1500;
    }

}
