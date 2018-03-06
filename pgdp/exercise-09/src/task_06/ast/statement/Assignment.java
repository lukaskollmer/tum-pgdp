package task_06.ast.statement;

import task_06.ast.expression.Expression;
import task_06.compiler.Visitor;

public class Assignment implements Statement {

    private final String variableName;
    private final Expression expression;


    public Assignment(String variableName, Expression expression) {

        this.variableName = variableName;
        this.expression = expression;
    }


    public String getVariableName() {
        return variableName;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }
}
