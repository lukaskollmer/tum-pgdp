package task_07.ast;

import task_07.Util;
import task_07.ast.expression.Expression;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

public class Variable implements Expression {

    public final String name;

    public Variable(String name) {
        this.name = name;
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
        return Util.f("<Variable name=%s>", name);
    }
}
