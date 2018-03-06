package task_09.ast.expression;

import task_09.Util;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

public class Number implements Expression {

    public final int value;

    public Number(int value) {
        this.value = value;
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
        return Util.f("<Expression.Number value=%s>", value);
    }
}
