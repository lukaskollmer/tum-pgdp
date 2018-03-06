package task_07.ast.expression;

import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

import static task_07.Util.f;

public class ArrayLength_inline implements Expression {

    public final Expression targetPointerExpression;

    public ArrayLength_inline(Expression targetPointerExpression) {
        this.targetPointerExpression = targetPointerExpression;
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
        return f("<Expression.ArrayLength_inline arrayPointerExpression=%s >", targetPointerExpression);
    }
}
