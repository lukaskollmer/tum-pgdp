package task_06.ast;

import task_06.ast.expression.Expression;
import task_06.compiler.Visitor;

public class Variable implements Expression {

    private final String name;

    public Variable(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }
}
