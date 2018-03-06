package task_09.ast;

import task_09.Util;
import task_09.ast.formatter.FormatVisitor;
import task_09.ast.formatter.Formattable;
import task_09.ast.statement.ClassDescriptor;
import task_09.compiler.Visitable;
import task_09.compiler.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Program implements Visitable, Formattable {
    public final List<Function> functions;
    public final List<ClassDescriptor> classDescriptors;

    public Program(Function... functions) {
        this.functions = Arrays.asList(functions);
        this.classDescriptors = new ArrayList<>();
    }

    public Program(List<Function> functions, List<ClassDescriptor> classDescriptors) {
        this.functions = functions;
        this.classDescriptors = classDescriptors;
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
