package task_07.ast.expression;

import task_07.ast.Operator;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

import static task_07.Util.f;

public class ArrayCreation implements Expression, Operator {

    public final Expression sizeExpression;

    public ArrayCreation(Expression sizeExpression) {
        this.sizeExpression = sizeExpression;
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
        return String.format("<Expression.ArrayCreation size=%s >", this.sizeExpression);
    }

    @Override
    public int precedence() {
        return 1300;
    }

}
