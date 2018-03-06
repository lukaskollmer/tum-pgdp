package task_07.ast.formatter;

import task_07.Util;
import task_07.ast.*;
import task_07.ast.expression.*;
import task_07.ast.expression.Number;
import task_07.ast.statement.*;
import task_07.interpreter.Operation;
import task_07.interpreter.Stack;

import javax.swing.plaf.nimbus.State;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static task_07.Util.f;
import static task_07.Util.log;

public class FormatVisitor implements Formatter_protocol {

    public static class Options {
        public static int Indentation = 4;
    }

    public static String indent() {
        return String.join("", Collections.nCopies(Options.Indentation, " "));
    }

    private final StringBuilder code = new StringBuilder();

    public FormatVisitor() {}


    public void visit(Program program) {
        for (Function function : program.functions) {
            StringBuilder sb_function = new StringBuilder();
            sb_function.append(f("int %s(", function.name));

            for (int i = 0; i < function.parameters.size(); i++) {
                sb_function.append("int ").append(function.parameters.get(i));
                if (i < function.parameters.size() - 1) {
                    sb_function.append(", ");
                }
            }
            sb_function.append(") {\n");


            for (Declaration declaration : function.declarations) {
                sb_function.append(indent()).append("int ");

                for (int i = 0; i < declaration.names.size(); i++) {
                    sb_function.append(declaration.names.get(i));
                    if (i < declaration.names.size() - 1) {
                        sb_function.append(", ");
                    }
                }

                sb_function.append(";\n");
            }

            for (Statement statement : function.statements) {
                FormatVisitor _visitor = new FormatVisitor();
                statement.accept(_visitor);

                for (String line : _visitor.getResult().split("\n")) {
                    sb_function
                            .append(indent())
                            .append(line)
                            .append("\n");
                }
            }

            sb_function.append("}\n\n");

            code.append(sb_function.toString());
        }
    }








    //
    // Statements
    //



    @Override
    public void visit(Return statement) throws Error {
        code.append("return ");
        statement.expression.accept(this);
        code.append(";");
    }

    @Override
    public void visit(Asm asmStatement) throws Error {
        code.append(f("asm(\"%s\");", asmStatement.instruction.replace(" ", "_")));
    }

    @Override
    public void visit(Assignment assignment) throws Error {
        code.append(assignment.variableName).append(" = ");
        assignment.expression.accept(this);
        code.append(";");
    }

    @Override
    public void visit(Read readStatement) throws Error {
        code.append(f("%s = read();", readStatement.variableName));
    }

    @Override
    public void visit(Write writeStatement) throws Error {
        code.append("write(");
        writeStatement.expression.accept(this);
        code.append(");");
    }

    @Override
    public void visit(While whileStatement) throws Error {
        code.append("while (");
        whileStatement.condition.accept(this);
        code.append(")");

        if (whileStatement.body instanceof Composite) {
            code.append(" ");
        } else {
            code.append("\n");
        }

        code.append(indent());
        whileStatement.body.accept(this);
    }

    @Override
    public void visit(If ifStatement) throws Error {
        code.append("if (");
        ifStatement.condition.accept(this);
        code.append(")");

        if (ifStatement.thenBranch instanceof Composite) {
            code.append(" ");
        } else {
            code.append("\n");
            code.append(indent());
        }

        ifStatement.thenBranch.accept(this);

        if (ifStatement.elseBranch != null) {

            if (ifStatement.thenBranch instanceof Composite) {
                // add a space after the closing curly brace of the then statement
                code.append(" ");
            } else {
                code.append("\n");
            }

            code.append("else");
            if (ifStatement.elseBranch instanceof Composite) {
                code.append(" ");
            } else {
                code.append("\n");
                code.append(indent());
            }

            ifStatement.elseBranch.accept(this);
        }

        code.append("\n");
    }

    @Override
    public void visit(Composite composite) throws Error {
        code.append("{\n");

        for (Statement statement : composite.statements) {
            code.append(indent());
            statement.accept(this);
            code.append("\n"); // todo commenting this out could come back and haunt us
        }

        code.append("}");
    }

    @Override
    public void visit(ArrayElementGetter arrayElementGetter) throws Error {

        boolean shouldWrapInParens = shouldWrapInParens(arrayElementGetter, arrayElementGetter.targetObjectExpression);

        code.append(shouldWrapInParens ? "(" : "");
        arrayElementGetter.targetObjectExpression.accept(this);
        code.append(shouldWrapInParens ? ")" : "").append("[");
        arrayElementGetter.elementOffsetExpression.accept(this);
        code.append("]");
    }

    @Override
    public void visit(ArrayElementSetter arrayElementSetter) throws Error {

        code.append(arrayElementSetter.variableName).append("[");
        arrayElementSetter.offsetExpression.accept(this);
        code.append("] = ");
        arrayElementSetter.assignedValueExpression.accept(this);
        code.append(";");
    }









    //
    // Expressions
    //


    @Override
    public void visit(Variable variable) throws Error {
        this.code.append(variable.name);
    }

    @Override
    public void visit(Number number) throws Error {
        this.code.append(number.value);
    }

    @Override
    public void visit(Binary binaryExpression) throws Error {

        boolean shouldWrapLhs = shouldWrapInParens(binaryExpression.lhs, binaryExpression.binop);
        boolean shouldWrapRhs = shouldWrapInParens(binaryExpression.binop, binaryExpression.rhs);

        if (binaryExpression.rhs instanceof Binary && Arrays.asList(Binary.Binop.SUBTRACTION, Binary.Binop.DIVISION).contains(binaryExpression.binop)) {
            shouldWrapRhs = true;
        }

        this.code.append(shouldWrapLhs ? "(" : "");
        binaryExpression.lhs.accept(this);

        code
                .append(shouldWrapLhs ? ")" : "")
                .append(" ").append(binaryExpression.binop.symbol).append(" ")
                .append(shouldWrapRhs ? "(" : "");

        binaryExpression.rhs.accept(this);
        code.append(shouldWrapRhs ? ")" : "");

    }

    @Override
    public void visit(Call call) throws Error {
        code.append(call.functionName).append("(");

        for (int i = 0; i < call.arguments.size(); i++) {
            call.arguments.get(i).accept(this);

            if (i < call.arguments.size() - 1) code.append(", ");
        }

        code.append(")");
    }

    @Override
    public void visit(Unary unaryExpression) throws Error {
        StringBuilder sb_unary = new StringBuilder()
                .append("-")
                .append("(");

        FormatVisitor _visitor = new FormatVisitor();
        unaryExpression.expression.accept(_visitor);

        sb_unary.append(_visitor.getResult());
        sb_unary.append(")");

        //if (true) { code.append(sb_unary.toString()); return; }

        String w_outParens = sb_unary.toString()
                .replace("(", "")
                .replace(")", "");

        if (MathExpression.producesSameResult(sb_unary.toString(), w_outParens)) {
            this.code.append(w_outParens);
        } else {
            this.code.append(sb_unary.toString());
        }
    }

    @Override
    public void visit(Condition.True condition) throws Error {
        code.append("true");
    }

    @Override
    public void visit(Condition.False condition) throws Error {
        code.append("false");
    }

    @Override
    public void visit(Condition.Binary condition) throws Error {
        boolean shouldWrapLhs = shouldWrapInParens(condition.lhs, condition.operator);
        boolean shouldWrapRhs = shouldWrapInParens(condition.operator, condition.rhs);

        code.append(shouldWrapLhs ? "(" : "");
        condition.lhs.accept(this);

        code
                .append(shouldWrapLhs ? ")" : "")
                .append(f(" %s ", condition.operator.symbol))
                .append(shouldWrapRhs ? "(" : "");

        condition.rhs.accept(this);
        code.append(shouldWrapRhs ? ")" : "");
    }

    @Override
    public void visit(Condition.Comparison condition) throws Error {

        condition.lhs.accept(this);
        code.append(f(" %s ", condition.comparator.symbol));
        condition.rhs.accept(this);
    }

    @Override
    public void visit(Condition.Unary condition) throws Error {
        code.append("!(");
        condition.condition.accept(this);
        code.append(")");
    }

    @Override
    public void visit(ArrayCreation arrayCreation) throws Error {
        code.append("new int[");
        arrayCreation.sizeExpression.accept(this);
        code.append("]");
    }

    @Override
    public void visit(ArrayLength_inline arrayLength) throws Error {
        code.append("length(");
        arrayLength.targetPointerExpression.accept(this);
        code.append(")");
    }



    public String getResult() {
        return this.code.toString();
    }




    static boolean shouldWrapInParens(Operator this_op, Operator other_op) {
        return this_op.precedence() > other_op.precedence();
    }


    static boolean shouldWrapInParens(Operator this_op, Expression other_expr) {
        Operator operator = extract_op_if_possible(other_expr);
        return operator != null && shouldWrapInParens(this_op, (Operator) Util.nullCoalescing(operator, other_expr));
    }


    // same as above, but in reverse
    static boolean shouldWrapInParens(Expression this_expr, Operator other_op) {
        Operator operator = extract_op_if_possible(this_expr);
        return operator != null && shouldWrapInParens(other_op, (Operator) Util.nullCoalescing(operator, this_expr));
    }


    private static Operator extract_op_if_possible(Expression expression) {
        boolean isSupportedType =
                expression instanceof Binary ||
                        expression instanceof Condition.Binary ||
                        expression instanceof Unary ||
                        expression instanceof Operator;

        if (!isSupportedType) return null;

        Operator operator = null;
        if (expression instanceof Binary) {
            operator = ((Binary) expression).binop;
        } else if (expression instanceof Condition.Binary) {
            operator = ((Condition.Binary) expression).operator;
        } else if (expression instanceof Unary) {
            operator = ((Unary) expression).operator;
        } else {
            operator = (Operator) expression;
        }

        return operator;
    }
}
