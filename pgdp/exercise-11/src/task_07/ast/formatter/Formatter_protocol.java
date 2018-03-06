package task_07.ast.formatter;

import task_07.ast.Program;
import task_07.ast.Variable;
import task_07.ast.expression.*;
import task_07.ast.expression.Number;
import task_07.ast.statement.*;

public interface Formatter_protocol {

    void visit(Program program);


    // STATEMENTS
    void visit(Return statement) throws Error;
    void visit(Asm asmStatement) throws Error;
    void visit(Assignment assignment) throws Error;
    void visit(Read readStatement) throws Error;
    void visit(Write writeStatement) throws Error;
    void visit(While whileStatement) throws Error;
    void visit(If ifStatement) throws Error;
    void visit(Composite composite) throws Error;
    void visit(ArrayElementGetter arrayElementGetter) throws Error;
    void visit(ArrayElementSetter arrayElementSetter) throws Error;

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

    void visit(ArrayCreation arrayCreation) throws Error;
    void visit(ArrayLength_inline arrayLength) throws Error;
}
