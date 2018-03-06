package task_06.ast.expression;

import task_06.compiler.Visitor;

public class Number implements Expression {

    private final int value;

    public Number(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }
}
