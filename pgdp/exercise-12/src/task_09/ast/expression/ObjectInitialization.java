package task_09.ast.expression;

import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

import java.util.Collections;
import java.util.List;

public class ObjectInitialization implements Expression {

    public final String classname;
    public final List<Expression> parameters;


    public ObjectInitialization(String classname) {
        this(classname, Collections.emptyList());
    }

    public ObjectInitialization(String classname, List<Expression> parameters) {
        this.classname = classname;
        this.parameters = parameters;
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
        return String.format("<ast.ObjecrInitialization cls=%s parameters=%s", classname, parameters);
    }
}
