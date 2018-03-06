package task_06.ast.expression;

import task_06.compiler.Visitor;

public class Binary implements Expression {

    public enum Binop {
        ADDITION("ADD"),
        SUBTRACTION("SUB"),
        MULTIPLICATION("MUL"),
        DIVISION("DIV"),
        MODULO("MOD");

        public final String instruction;

        Binop(String instruction) {
            this.instruction = instruction;
        }
    }



    private final Expression lhs;
    private final Binop binop;
    private final Expression rhs;


    public Binary(Expression lhs, Binop binop, Expression rhs) {

        this.lhs = lhs;
        this.binop = binop;
        this.rhs = rhs;
    }


    public Expression getLhs() {
        return lhs;
    }

    public Binop getBinop() {
        return binop;
    }

    public Expression getRhs() {
        return rhs;
    }


    @Override
    public void accept(Visitor visitor) throws Visitor.Error {
        visitor.visit(this);
    }
}
