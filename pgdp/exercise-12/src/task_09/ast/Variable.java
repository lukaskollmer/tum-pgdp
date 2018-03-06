package task_09.ast;

import task_09.Util;
import task_09.ast.expression.Expression;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

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
