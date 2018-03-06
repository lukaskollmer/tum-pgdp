package task_07.ast.statement;

import task_07.Util;
import task_07.compiler.Visitor;

public class Read implements Statement {

    private final String variableName;


    public Read(String variableName) {
        this.variableName = variableName;
    }


    public String getVariableName() {
        return variableName;
    }


    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }


    @Override
    public String toString() {
        return Util.f("<Statement.Read variableName=%s >", variableName);
    }
}
