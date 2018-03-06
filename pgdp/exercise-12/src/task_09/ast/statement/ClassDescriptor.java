package task_09.ast.statement;

import task_09.ast.Declaration;
import task_09.ast.Function;

import java.util.ArrayList;
import java.util.List;

public class ClassDescriptor {

    public final String classname;
    public final String superclass;
    public final List<Declaration> ivars;
    public final List<Function> functions;

    public final List<String> ivarNames = new ArrayList<>(); // legacy


    public ClassDescriptor(String classname, String superclass, List<Declaration> ivars, List<Function> functions) {
        this.classname = classname;
        this.superclass = superclass;
        this.ivars = ivars;
        this.functions = functions;

        ivars.forEach(ivar -> this.ivarNames.add(ivar.names.get(0))); // legacy
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("<ast.ClassDescriptor name=%s, superclass=%s\n", classname, superclass));
        stringBuilder.append(String.format("  ivars: %s\n", this.ivars));
        stringBuilder.append(String.format("  methods: %s\n", this.functions));

        return stringBuilder.toString();
    }
}
