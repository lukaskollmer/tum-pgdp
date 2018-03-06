package task_09.ast.expression;

import task_09.ast.Operator;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

import static task_09.Util.f;

public class ArrayElementGetter implements Expression, Operator {

    public final Expression targetObjectExpression;
    public final Expression elementOffsetExpression;


    public ArrayElementGetter(Expression targetObjectExpression, Expression elementOffsetExpression) {
        this.targetObjectExpression = targetObjectExpression;
        this.elementOffsetExpression = elementOffsetExpression;
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
        return new StringBuilder()
                .append("<Expression.ArrayElementGetter\n")
                .append(f("  target=%s\n", targetObjectExpression))
                .append(f("  offset=%s\n", elementOffsetExpression))
                .append(">")
                .toString();
    }

    @Override
    public int precedence() {
        return 1500;
    }
}
