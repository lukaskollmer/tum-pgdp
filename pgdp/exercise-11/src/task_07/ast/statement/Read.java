package task_07.ast.statement;

import task_07.Util;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

import static task_07.Util.f;

public class Read implements Statement {

    public final String variableName;


    public Read(String variableName) {
        this.variableName = variableName;
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
        return f("<Statement.Read variableName=%s >", variableName);
    }
}
