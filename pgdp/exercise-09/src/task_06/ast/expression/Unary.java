package task_06.ast.expression;

import task_06.compiler.Visitor;

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
}
