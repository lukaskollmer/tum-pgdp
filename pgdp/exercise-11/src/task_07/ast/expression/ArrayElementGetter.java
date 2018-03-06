package task_07.ast.expression;

import task_07.ast.Operator;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

import static task_07.Util.f;

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
