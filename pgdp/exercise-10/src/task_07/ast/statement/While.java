package task_07.ast.statement;

import task_07.Util;
import task_07.ast.expression.Condition;
import task_07.compiler.Visitor;

public class While implements Statement {

    private final Condition condition;
    private final Statement body;


    public While(Condition condition, Statement body) {

        this.condition = condition;
        this.body = body;
    }


    public Condition getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
    }


    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<Statement.While\n");
        stringBuilder.append(Util.f("  cond=%s\n", condition));
        stringBuilder.append(Util.f("  body=%s\n", body));
        stringBuilder.append(">");

        return stringBuilder.toString();
    }
}
