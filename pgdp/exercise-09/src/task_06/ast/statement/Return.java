package task_06.ast.statement;

import task_06.ast.expression.Expression;
import task_06.compiler.Visitor;

public class Return implements Statement {

    private final Expression expression;

    public Return(Expression expression) {
        this.expression = expression;
    }


    public Expression getExpression() {
        return expression;
    }


    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }
}
