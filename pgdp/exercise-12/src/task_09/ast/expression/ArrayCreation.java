package task_09.ast.expression;

import task_09.ast.Operator;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;


public class ArrayCreation implements Expression, Operator {

    public final String typename;
    public final Expression sizeExpression;

    // legacy
    public ArrayCreation(Expression sizeExpression) {
        this("int", sizeExpression);
    }

    public ArrayCreation(String typename, Expression sizeExpression) {
        this.typename = typename;
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
