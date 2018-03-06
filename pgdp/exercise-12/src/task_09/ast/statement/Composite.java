package task_09.ast.statement;

import task_09.Util;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

import java.util.Arrays;
import java.util.List;

public class Composite implements Statement {

    public final List<Statement> statements;


    public Composite(Statement... statements) {
        this.statements = Arrays.asList(statements);
    }

    public Composite(List<Statement> statements) {
        this.statements = statements;
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
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<Statement.Composite (\n");

        for (Statement statement : statements) {
            stringBuilder.append(Util.f("  %s\n", statement));
        }

        stringBuilder.append(")>");

        return stringBuilder.toString();
    }
}
