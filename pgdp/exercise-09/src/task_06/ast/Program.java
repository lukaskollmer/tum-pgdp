package task_06.ast;

import task_06.compiler.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Program implements Visitable {
    private final List<Function> functions = new ArrayList<>();

    public Program(Function... functions) {
        this.functions.addAll(Arrays.asList(functions));
    }


    public List<Function> getFunctions() {
        return this.functions;
    }

    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }
}
