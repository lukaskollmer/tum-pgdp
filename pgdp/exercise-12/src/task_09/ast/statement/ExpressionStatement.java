package task_09.ast.statement;

import task_09.ast.expression.Expression;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

public class ExpressionStatement implements Statement {

    public final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }


    @Override
    public void accept(FormatVisitor formatVisitor) {
        formatVisitor.visit(this);
    }

    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }
}
