package task_09.ast.statement;

import task_09.ast.Operator;
import task_09.ast.expression.Expression;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

import static task_09.Util.f;

public class Assignment implements Statement, Operator {

    public final String variableName;
    public final Expression expression;


    public Assignment(String variableName, Expression expression) {

        this.variableName = variableName;
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
        return f("<Statement.Assignment variableName=%s expr=%s >", variableName, expression);
    }

    @Override
    public int precedence() {
        return 100;
    }

}
