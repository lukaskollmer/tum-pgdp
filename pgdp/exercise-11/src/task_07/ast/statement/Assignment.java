package task_07.ast.statement;

import task_07.ast.Operator;
import task_07.ast.expression.Expression;
import task_07.ast.formatter.FormatVisitor;
import task_07.compiler.Visitor;

import static task_07.Util.f;

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
