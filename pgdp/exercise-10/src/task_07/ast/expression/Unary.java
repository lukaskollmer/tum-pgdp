package task_07.ast.expression;

import task_07.Util;
import task_07.compiler.Visitor;

public class Unary implements Expression {
    public enum Unop {
        MINUS
    }

    private final Unop operator;
    private final Expression expression;


    public Unary(Unop operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }


    public Unop getOperator() {
        return operator;
    }

    public Expression getExpression() {
        return expression;
    }


    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }


    @Override
    public String toString() {
        return Util.f("<Expression.Unary expr=%s >", expression);
    }
}
