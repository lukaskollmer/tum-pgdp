package task_07.ast;

import task_07.Util;
import task_07.ast.statement.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Function {

    public final String name;
    public final List<String> parameters;
    public final List<Declaration> declarations;
    public final List<Statement> statements;

    public Function(String name, List<String> parameters, List<Declaration> declarations, List<Statement> statements) {
        this.name = name;

        this.parameters   = Util.nullCoalescing(parameters,   new ArrayList<>());
        this.declarations = Util.nullCoalescing(declarations, new ArrayList<>());
        this.statements   = Util.nullCoalescing(statements,   new ArrayList<>());
    }

    public Function(String name, String[] parameters, Declaration[] declarations, Statement[] statements) {
        this(
                name,
                Arrays.asList(parameters),
                Arrays.asList(declarations),
                Arrays.asList(statements)
        );
    }



    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(Util.f("<Function name=%s parameters=%s declarations=%s\n", name, parameters, declarations));

        for (Statement statement : statements) {
            stringBuilder.append(Util.f("  %s\n", statement));
        }

        stringBuilder.append(">");

        return stringBuilder.toString();
    }
}
