package task_09.ast.statement;

import task_09.ast.expression.Expression;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

public class ArrayElementSetter implements Statement {

    public final Expression target;
    public final Expression offsetExpression;
    public final Expression assignedValueExpression;


    public ArrayElementSetter(Expression target, Expression offsetExpression, Expression assignedValueExpression) {
        this.target = target;
        this.offsetExpression = offsetExpression;
        this.assignedValueExpression = assignedValueExpression;
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
        return String.format("<ast.ArrayElementSetter target=%s offset=%s value=%s", target, offsetExpression, assignedValueExpression);
    }
}