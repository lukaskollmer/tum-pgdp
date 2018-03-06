package task_09.ast.statement;

import task_09.Util;
import task_09.ast.expression.Condition;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

public class While implements Statement {

    public final Condition condition;
    public final Statement body;


    public While(Condition condition, Statement body) {

        this.condition = condition;
        this.body = body;
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

        stringBuilder.append("<Statement.While\n");
        stringBuilder.append(Util.f("  cond=%s\n", condition));
        stringBuilder.append(Util.f("  body=%s\n", body));
        stringBuilder.append(">");

        return stringBuilder.toString();
    }
}
