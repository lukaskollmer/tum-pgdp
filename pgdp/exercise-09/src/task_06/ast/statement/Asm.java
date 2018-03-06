package task_06.ast.statement;

import task_06.ast.expression.Expression;
import task_06.compiler.Visitor;

// custom class that represents an assembly instruction
// this is similar to the `asm` keyword in c++
// fun fact: `Asm` is the only class that's both a Statement and an Expression
public class Asm implements Statement, Expression {

    private final String instruction;

    public Asm(String instruction) {
        this.instruction = instruction;
    }


    public String getInstruction() {
        return instruction;
    }


    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }
}
