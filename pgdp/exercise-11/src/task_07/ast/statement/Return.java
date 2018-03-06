package task_07.ast.statement;

import task_07.Util;
import task_07.ast.expression.Expression;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

import static task_07.Util.f;

public class Return implements Statement {

    public final Expression expression;

    public Return(Expression expression) {
        this.expression = expression;
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
        return f("<Statement.Return expr=%s", expression);
    }
}
