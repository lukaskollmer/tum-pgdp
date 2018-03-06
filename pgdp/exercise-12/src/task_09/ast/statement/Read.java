package task_09.ast.statement;

import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

import static task_09.Util.f;

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
