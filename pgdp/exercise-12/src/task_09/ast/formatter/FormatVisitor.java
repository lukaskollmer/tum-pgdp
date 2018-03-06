package task_09.ast.formatter;

import task_09.Util;
import task_09.ast.*;
import task_09.ast.expression.*;
import task_09.ast.expression.Number;
import task_09.ast.statement.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static task_09.Util.f;

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


        BiConsumer<List<Function>, StringBuilder> handleFunctions = (functions, dest) -> {
            for (Function fn : functions) {
                String formatted = _formatFunction(fn);
                for (String line : formatted.split("\n")) {
                    if (fn.isInstanceMethod) dest.append(indent());
                    dest.append(line).append("\n");
                }
                dest.append("\n");
            }
        };


        for (ClassDescriptor classDescriptor : program.classDescriptors) {
            StringBuilder sb_class = new StringBuilder();


            //
            // [class] declaration

            sb_class.append(f("class %s", classDescriptor.classname));

            if (classDescriptor.superclass != null) {
                sb_class.append(f(" extends %s", classDescriptor.superclass));
            }

            sb_class.append(" {\n");


            //
            // [class] ivars

            for (Declaration ivar : classDescriptor.ivars) {
                sb_class.append(f("%s%s %s;\n", indent(), ivar.typename, ivar.getName()));
            }
            if (!classDescriptor.ivars.isEmpty()) sb_class.append("\n");

            //
            // [class] instance methods

            handleFunctions.accept(classDescriptor.functions, sb_class);


            sb_class.append("}\n\n"); // [class] closing braces


            code.append(sb_class);
        }


        handleFunctions.accept(program.functions, code);




        if (true) return;



    }




    private String _formatFunction(Function function) {
        StringBuilder sb_function = new StringBuilder();
        sb_function.append(f("%s %s(", function.returnType, function.name));

        for (int i = 0; i < function.parameters.size(); i++) {
            Declaration param = function.parameters.get(i);
            sb_function.append(f("%s %s", param.typename, param.getName()));
            if (i < function.parameters.size() - 1) {
                sb_function.append(", ");
            }
        }
        sb_function.append(") {\n");


        for (Declaration declaration : function.declarations) {
            sb_function
                    .append(indent())
                    .append(declaration.typename).append(" ")
                    .append(String.join(", ", declaration.names))
                    .append(";\n");
        }

        if (!function.declarations.isEmpty()) {
            sb_function.append("\n"); // insert a newline between the local variable declarations and the statements
        }

        // TODO what about inserting a newline before the return statement? (only if #statements is greater than some magic number)

        for (Statement statement : function.statements) {
            FormatVisitor _visitor = new FormatVisitor();
            statement.accept(_visitor);

            for (String line : _visitor.getResult().split("\n")) {
                if (line.isEmpty()) continue;
                sb_function
                        .append(indent())
                        .append(line)
                        .append("\n");
            }
        }

        sb_function.append("}\n\n");

        return sb_function.toString();
    }


    //
    // OO
    //


    @Override
    public void visit(ExpressionStatement expressionStatement) {
        expressionStatement.expression.accept(this);
        code.append(";");
    }

    @Override
    public void visit(ObjectInitialization objectInitialization) {
        code
                .append(f("new %s(", objectInitialization.classname))
                .append(_handleFunctionCallArguments(objectInitialization.parameters))
                .append(")");
    }

    @Override
    public void visit(InstanceMethodCall instanceMethodCall) {
        code
                .append(instanceMethodCall.targetName)
                .append(".")
                .append(instanceMethodCall.selector)
                .append("(")
                .append(_handleFunctionCallArguments(instanceMethodCall.arguments))
                .append(")");
    }


    private String _handleFunctionCallArguments(List<Expression> args) {
        List<String> args_formatted = new ArrayList<>();
        args.forEach(arg -> args_formatted.add(Formatter.format(arg)));

        return String.join(", ", args_formatted);
    }


    //
    // Statements
    //



    @Override
    public void visit(Return statement) {
        code.append("return ");
        statement.expression.accept(this);
        code.append(";");
    }

    @Override
    public void visit(Asm asmStatement) {
        if (!asmStatement.instruction.isEmpty()) {
            code.append(f("asm(\"%s\");", asmStatement.instruction.replace(" ", "_")));
        }
    }

    @Override
    public void visit(Assignment assignment) {
        code.append(assignment.variableName).append(" = ");
        assignment.expression.accept(this);
        code.append(";");
    }

    @Override
    public void visit(Read readStatement) {
        code.append(f("%s = read();", readStatement.variableName));
    }

    @Override
    public void visit(Write writeStatement) {
        code.append("write(");
        writeStatement.expression.accept(this);
        code.append(");");
    }

    @Override
    public void visit(While whileStatement) {
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
    public void visit(If ifStatement) {
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
    public void visit(Composite composite) {
        code.append("{\n");

        for (Statement statement : composite.statements) {
            code.append(indent());
            statement.accept(this);
            code.append("\n");
        }

        code.append("}");
    }

    @Override
    public void visit(ArrayElementGetter arrayElementGetter) {

        boolean shouldWrapInParens = shouldWrapInParens(arrayElementGetter, arrayElementGetter.targetObjectExpression);

        code.append(shouldWrapInParens ? "(" : "");
        arrayElementGetter.targetObjectExpression.accept(this);
        code.append(shouldWrapInParens ? ")" : "").append("[");
        arrayElementGetter.elementOffsetExpression.accept(this);
        code.append("]");
    }

    @Override
    public void visit(ArrayElementSetter arrayElementSetter) {

        arrayElementSetter.target.accept(this);
        code.append("[");
        arrayElementSetter.offsetExpression.accept(this);
        code.append("] = ");
        arrayElementSetter.assignedValueExpression.accept(this);
        code.append(";");
    }









    //
    // Expressions
    //


    @Override
    public void visit(Variable variable) {
        this.code.append(variable.name);
    }

    @Override
    public void visit(Number number) {
        this.code.append(number.value);
    }

    @Override
    public void visit(Binary binaryExpression) {

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
    public void visit(Call call) {
        code.append(call.functionName).append("(");

        for (int i = 0; i < call.arguments.size(); i++) {
            call.arguments.get(i).accept(this);

            if (i < call.arguments.size() - 1) code.append(", ");
        }

        code.append(")");
    }

    @Override
    public void visit(Unary unaryExpression) {
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
    public void visit(Condition.True condition) {
        code.append("true");
    }

    @Override
    public void visit(Condition.False condition) {
        code.append("false");
    }

    @Override
    public void visit(Condition.Binary condition) {
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
    public void visit(Condition.Comparison condition) {

        condition.lhs.accept(this);
        code.append(f(" %s ", condition.comparator.symbol));
        condition.rhs.accept(this);
    }

    @Override
    public void visit(Condition.Unary condition) {
        code.append("!(");
        condition.condition.accept(this);
        code.append(")");
    }

    @Override
    public void visit(ArrayCreation arrayCreation) {
        code.append(f("new %s[", arrayCreation.typename));
        arrayCreation.sizeExpression.accept(this);
        code.append("]");
    }

    @Override
    public void visit(ArrayLength_inline arrayLength) {
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
