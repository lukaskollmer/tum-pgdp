package task_06.ast.statement;

import task_06.ast.expression.Expression;
import task_06.compiler.Visitor;

public class Write implements Statement {

    private final Expression expression;

    public Write(Expression expression) {

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
