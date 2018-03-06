package task_09.ast.expression;

import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

import static task_09.Util.f;

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
