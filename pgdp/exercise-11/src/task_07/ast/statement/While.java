package task_07.ast.statement;

import task_07.Util;
import task_07.ast.expression.Condition;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

import static task_07.ast.formatter.FormatVisitor.indent;

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
