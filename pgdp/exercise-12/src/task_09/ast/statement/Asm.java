package task_09.ast.statement;

import task_09.ast.expression.Expression;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

import static task_09.Util.f;

// custom class that represents an assembly instruction
// this is similar to the `asm` keyword in c++
// fun fact: `Asm` is the only class that's both a Statement and an Expression
public class Asm implements Statement, Expression {

    public final String instruction;

    public Asm(String instruction) {
        this.instruction = instruction;
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
        return f("<Statement.Asm instruction='%s'>", instruction);
    }
}
