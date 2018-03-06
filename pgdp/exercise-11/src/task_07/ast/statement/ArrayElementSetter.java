package task_07.ast.statement;

import task_07.ast.expression.Expression;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

import static task_07.Util.f;

public class ArrayElementSetter implements Statement {

    public final String variableName;
    public final Expression offsetExpression;
    public final Expression assignedValueExpression;


    public ArrayElementSetter(String variableName, Expression offsetExpression, Expression assignedValueExpression) {
        this.variableName = variableName;
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
}