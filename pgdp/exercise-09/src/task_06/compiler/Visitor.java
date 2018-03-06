package task_06.compiler;

import task_06.ast.*;
import task_06.ast.expression.Number;
import task_06.ast.statement.*;
import task_06.ast.expression.*;

public interface Visitor {

    class Error extends Exception {
        Error(String format, Object... args) {
            super(String.format(format, args));
        }
    }

    void visit(Program program) throws Error;

    // STATEMENTS
    void visit(Return statement) throws Error;
    void visit(Asm asmStatement) throws Error;
    void visit(Assignment assignment) throws Error;
    void visit(Read readStatement) throws Error;
    void visit(Write writeStatement) throws Error;
    void visit(While whileStatement) throws Error;
    void visit(If ifStatement) throws Error;;
    void visit(Composite composite) throws Error;

    // EXPRESSIONS
    void visit(Variable variable) throws Error;
    void visit(Number number) throws Error;
    void visit(Binary binaryExpression) throws Error;
    void visit(Call call) throws Error;
    void visit(Unary unaryExpression) throws Error;

    void visit(Condition.True condition) throws Error;
    void visit(Condition.False condition) throws Error;
    void visit(Condition.Binary condition) throws Error;
    void visit(Condition.Comparison condition) throws Error;
    void visit(Condition.Unary condition) throws Error;
}
