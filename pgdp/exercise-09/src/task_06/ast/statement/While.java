package task_06.ast.statement;

import task_06.ast.expression.Condition;
import task_06.compiler.Visitor;

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
}
