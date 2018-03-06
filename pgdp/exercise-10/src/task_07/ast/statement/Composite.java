package task_07.ast.statement;

import task_07.Util;
import task_07.compiler.Visitor;

import java.util.Arrays;
import java.util.List;

public class Composite implements Statement {

    private final List<Statement> statements;


    public Composite(Statement... statements) {
        this.statements = Arrays.asList(statements);
    }

    public Composite(List<Statement> statements) {
        this.statements = statements;
    }


    public List<Statement> getStatements() {
        return statements;
    }


    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
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
