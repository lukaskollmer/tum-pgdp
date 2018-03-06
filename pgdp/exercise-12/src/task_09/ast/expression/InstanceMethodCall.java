package task_09.ast.expression;

import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

import java.util.Collections;
import java.util.List;

public class InstanceMethodCall implements Expression {

    public final String targetName;
    public final String selector;
    public final List<Expression> arguments;
    public boolean unusedReturnValue = false;


    public InstanceMethodCall(String targetName, String selector) {
        this(targetName, selector, Collections.emptyList());
    }


    public InstanceMethodCall(String targetName, String selector, List<Expression> arguments) {
        this.targetName= targetName;
        this.selector = selector;
        this.arguments = arguments;
    }


    @Override
    public void accept(FormatVisitor formatVisitor) {
        formatVisitor.visit(this);
    }

    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("<ast.InstanceMethodCall target=%s selector=%s parameters=%s>", targetName, selector, arguments);
    }
}
