package task_07.ast.statement;

import task_07.Util;
import task_07.ast.expression.Expression;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.Visitor;

import static task_07.Util.f;

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
