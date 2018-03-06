package task_06.ast.statement;

import task_06.compiler.Visitor;

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
}
