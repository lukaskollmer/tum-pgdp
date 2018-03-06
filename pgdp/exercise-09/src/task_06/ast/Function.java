package task_06.ast;

import task_06.Util;
import task_06.ast.statement.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Function {

    private final String name;
    private final List<String> parameters;
    private final List<Declaration> declarations;
    private final List<Statement> statements;

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


    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
