package task_09.ast.formatter;

import task_09.ast.Program;
import task_09.ast.Variable;
import task_09.ast.expression.*;
import task_09.ast.expression.Number;
import task_09.ast.statement.*;

public interface Formatter_protocol {

    void visit(Program program);


    // STATEMENTS
    void visit(Return statement);
    void visit(Asm asmStatement);
    void visit(Assignment assignment);
    void visit(Read readStatement);
    void visit(Write writeStatement);
    void visit(While whileStatement);
    void visit(If ifStatement);
    void visit(Composite composite);
    void visit(ArrayElementGetter arrayElementGetter);
    void visit(ArrayElementSetter arrayElementSetter);

    // EXPRESSIONS
    void visit(Variable variable);
    void visit(Number number);
    void visit(Binary binaryExpression);
    void visit(Call call);
    void visit(Unary unaryExpression);

    void visit(Condition.True condition);
    void visit(Condition.False condition);
    void visit(Condition.Binary condition);
    void visit(Condition.Comparison condition);
    void visit(Condition.Unary condition);

    void visit(ArrayCreation arrayCreation);
    void visit(ArrayLength_inline arrayLength);

    void visit(ObjectInitialization objectInitialization);
    void visit(InstanceMethodCall instanceMethodCall);
    void visit(ExpressionStatement expressionStatement);
}
