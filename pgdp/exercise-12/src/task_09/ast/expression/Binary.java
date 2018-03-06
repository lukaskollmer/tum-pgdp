package task_09.ast.expression;

import task_09.Util;
import task_09.ast.Operator;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

public class Binary implements Expression {

    public enum Binop implements Operator {
        MULTIPLICATION  ("*", "MUL", 1203),
        DIVISION        ("/", "DIV", 1202),
        MODULO          ("%", "MOD", 1201),

        ADDITION        ("+", "ADD", 1102),
        SUBTRACTION     ("-", "SUB", 1101);

        public final String symbol;
        public final String instruction;
        public final int precedence;

        Binop(String symbol, String instruction, int precedence) {
            this.symbol = symbol;
            this.instruction = instruction;
            this.precedence = precedence;
        }

        @Override
        public int precedence() {
            return precedence;
        }
    }


    public final Expression lhs;
    public final Binop binop;
    public final Expression rhs;


    public Binary(Expression lhs, Binop binop, Expression rhs) {

        this.lhs = lhs;
        this.binop = binop;
        this.rhs = rhs;
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
        return Util.f("<Expression.Binary binop=%s\n  lhs=%s\n  rhs=%s\n>", binop, lhs, rhs);
    }
}
