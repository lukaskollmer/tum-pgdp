package task_07.ast;

import task_07.Util;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.compiler.*;

import java.util.Arrays;
import java.util.List;

public class Program implements Visitable, Formattable {
    public final List<Function> functions;

    public Program(Function... functions) {
        this.functions = Arrays.asList(functions);
    }

    public Program(List<Function> functions) {
        this.functions = functions;
    }



    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }

    public void accept(FormatVisitor formatVisitor) {
        formatVisitor.visit(this);
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<Program functions=(\n");

        for (Function function : functions) {
            stringBuilder.append(Util.f("  %s\n", function));
        }

        stringBuilder.append(")>");

        return stringBuilder.toString();
    }
}
