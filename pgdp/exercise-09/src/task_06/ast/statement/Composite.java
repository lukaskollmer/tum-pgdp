package task_06.ast.statement;

import task_06.compiler.Visitor;

import java.util.Arrays;
import java.util.List;

public class Composite implements Statement {

    private final List<Statement> statements;


    public Composite(Statement... statements) {
        this.statements = Arrays.asList(statements);
    }


    public List<Statement> getStatements() {
        return statements;
    }


    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }
}
