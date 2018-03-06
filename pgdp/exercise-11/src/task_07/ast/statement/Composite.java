package task_07.ast.statement;

import task_07.Util;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

import java.util.Arrays;
import java.util.List;

import static task_07.ast.formatter.FormatVisitor.indent;

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
