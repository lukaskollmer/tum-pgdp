package task_09.ast;

import task_09.Util;
import task_09.ast.statement.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Function {

    public final String name;
    public final List<Declaration> parameters;
    public final List<Declaration> declarations;
    public final List<Statement> statements;
    public final String returnType;

    public final boolean isInitializer;
    public final boolean isInstanceMethod;
    public final String classname;

    public Function(String name, List<String> parameters, List<Declaration> declarations, List<Statement> statements) {
        this(
                "?",
                name,
                false,
                false,
                null,
                parameters.stream().map(n -> new Declaration("int", Collections.singletonList(n))).collect(Collectors.toList()), // java is such a fucking joke
                declarations,
                statements
        );
    }

    public Function(String returnType, String name, boolean isInitializer, boolean isInstanceMethod, String classname, List<Declaration> parameters, List<Declaration> declarations, List<Statement> statements) {
        this.returnType = returnType;
        this.name = name;
        this.isInitializer = isInitializer;
        this.isInstanceMethod = isInstanceMethod;
        this.classname = classname;

        this.parameters   = Util.nullCoalescing(parameters,   new ArrayList<>());
        this.declarations = Util.nullCoalescing(declarations, new ArrayList<>());
        this.statements   = Util.nullCoalescing(statements,   new ArrayList<>());
    }


    public Function(String name, String[] parameters, Declaration[] declarations, Statement[] statements) {
        this(
                "?",
                name,
                false,
                false,
                null,
                Arrays.stream(parameters).map(n -> new Declaration("int", Collections.singletonList(n))).collect(Collectors.toList()),
                Arrays.asList(declarations),
                Arrays.asList(statements)
        );
    }


    public String mangledName() {
        return Function.mangleName(classname, name, isInitializer, isInstanceMethod);
    }



    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(Util.f("<Function name=%s arguments=%s declarations=%s\n", name, parameters, declarations));

        for (Statement statement : statements) {
            stringBuilder.append(Util.f("  %s\n", statement));
        }

        stringBuilder.append(">");

        return stringBuilder.toString();
    }


    public static String mangleName(String classname, String selector, boolean isInitializer, boolean isInstanceMethod) {
        if (isInitializer) {
            // if the function is an initializer, the function name is the name of its class
            return selector + "_init";
        } else if (isInstanceMethod) {
            return classname + "_" + selector;
        }
        return selector;
    }
}
