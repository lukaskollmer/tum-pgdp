package task_07.ast.expression;

import task_07.Util;
import task_07.ast.Operator;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formattable;
import task_07.ast.formatter.MathExpression;
import task_07.compiler.Visitor;

public class Unary implements Expression {
    public enum Unop implements Operator {
        MINUS;

        @Override
        public int precedence() {
            return 1305;
        }
    }

    public final Unop operator;
    public final Expression expression;


    public Unary(Unop operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }



    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }

    @Override
    public void accept(FormatVisitor formatVisitor) {
        formatVisitor.visit(this);
    }


    @Override
    public String toString() {
        return Util.f("<Expression.Unary expr=%s >", expression);
    }
}
