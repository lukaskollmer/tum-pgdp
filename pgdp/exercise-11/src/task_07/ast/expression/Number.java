package task_07.ast.expression;

import task_07.Util;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

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
