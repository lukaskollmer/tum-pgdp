package task_07.ast.statement;

import task_07.Util;
import task_07.ast.expression.Condition;
import task_07.compiler.Visitor;

public class If implements Statement {

    private final Condition condition;
    private final Statement thenBranch; // either a single statement (aka a class implementing the protocol) or a Composite
    private final Statement elseBranch; // null if no else branch passed


    public If(Condition condition, Statement thenBranch) {
        this.condition  = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = null;
    }

    public If(Condition condition, Statement thenBranch, Statement elseBranch) {
        this.condition  = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }


    public Condition getCondition() {
        return condition;
    }

    public Statement getThenBranch() {
        return thenBranch;
    }

    public Statement getElseBranch() {
        return elseBranch;
    }


    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<Statement.If\n");
        stringBuilder.append(Util.f("  condition=%s\n", condition));
        stringBuilder.append(Util.f("  thenBranch=%s\n", thenBranch));
        stringBuilder.append(Util.f("  elseBranch=%s\n", elseBranch));
        stringBuilder.append(">");

        return stringBuilder.toString();
    }
}
