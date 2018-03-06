package task_09.ast.expression;

import task_09.ast.Operator;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

import java.util.Arrays;
import java.util.List;

import static task_09.Util.f;

public class Call implements Expression, Operator {

    public final String target;     // only nonnull if this is a method call
    public final String functionName;
    public final List<Expression> arguments;


    public Call(String functionName, Expression... arguments) {
        this(functionName, Arrays.asList(arguments));
    }

    public Call(String functionName, List<Expression> arguments) {
        this(null, functionName, arguments);
    }

    public Call(String target, String functionName, List<Expression> arguments) {
        this.target = target;
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
