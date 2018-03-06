package task_09.ast.statement;

import task_09.Util;
import task_09.ast.expression.Condition;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

public class If implements Statement {

    public final Condition condition;
    public final Statement thenBranch; // either a single statement (aka a class implementing the protocol) or a Composite
    public final Statement elseBranch; // null if no else branch passed


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

        stringBuilder.append("<Statement.If\n");
        stringBuilder.append(Util.f("  condition=%s\n", condition));
        stringBuilder.append(Util.f("  thenBranch=%s\n", thenBranch));
        stringBuilder.append(Util.f("  elseBranch=%s\n", elseBranch));
        stringBuilder.append(">");

        return stringBuilder.toString();
    }
}
