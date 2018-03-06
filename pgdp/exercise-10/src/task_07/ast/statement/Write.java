package task_07.ast.statement;

import task_07.Util;
import task_07.ast.expression.Expression;
import task_07.compiler.Visitor;

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

    @Override
    public String toString() {
        return Util.f("<Statement.Write expr=%s", expression);
    }
}
