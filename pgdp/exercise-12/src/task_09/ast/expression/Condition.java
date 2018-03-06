package task_09.ast.expression;

import task_09.ast.Operator;
import task_09.ast.formatter.FormatVisitor;
import task_09.compiler.Visitor;

import java.util.Arrays;
import java.util.List;

import static task_09.Util.f;


public interface Condition extends Expression {

    class True implements Condition {
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
            return "<Condition.True>";
        }
    }



    class False implements Condition {
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
            return "<Condition.False>";
        }
    }



    class Binary implements Condition {
        public enum Bbinop implements Operator {
            AND("&&", 400),
            OR("||", 300);

            public final String symbol;
            public final int precedence;

            Bbinop(String symbol, int precedence) {
                this.symbol = symbol;
                this.precedence = precedence;
            }

            @Override
            public int precedence() {
                return this.precedence;
            }

        }

        public final Condition lhs;
        public final Bbinop operator;
        public final Condition rhs;

        public Binary(Condition lhs, Bbinop operator, Condition rhs) {
            this.lhs = lhs;
            this.operator = operator;
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
            return new StringBuilder("<Condition.Binary\n")
                    .append(f("  lhs=%s\n", lhs))
                    .append(f("  operator=%s\n", operator))
                    .append(f("  rhs=%s", rhs))
                    .toString();
        }
    }



    class Comparison implements Condition {
        public enum Comparator {
            EQUALS("==", "EQ"),
            NOT_EQUALS("!=", "EQ", "NOT"),
            LESS_EQUAL("<=", "LE"),
            LESS("<", "LT"),
            GREATER_EQUAL(">=", "LT", "NOT"),
            GREATER(">", "LE", "NOT");

            public final String symbol;
            public final List<String> instructions;

            Comparator(String symbol, String... instructions) {
                this.symbol = symbol;
                this.instructions = Arrays.asList(instructions);
            }
        }



        public final Expression lhs;
        public final Comparator comparator;
        public final Expression rhs;


        public Comparison(Expression lhs, Comparator comparator, Expression rhs) {
            this.lhs = lhs;
            this.comparator = comparator;
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
            return new StringBuilder("<Condition.Comparison\n")
                    .append(f("  lhs=%s\n", lhs))
                    .append(f("  comparator=%s\n", comparator))
                    .append(f("  rhs=%s", rhs))
                    .toString();
        }
    }



    class Unary implements Condition {
        public enum Bunop {
            NOT
        }

        public final Bunop operator;
        public final Condition condition;

        public Unary(Bunop operator, Condition condition) {
            this.operator = operator;
            this.condition = condition;
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
            return new StringBuilder("<Condition.Unary\n")
                    .append(f("  operator=%s\n", operator))
                    .append(f("  condition=%s", condition))
                    .toString();
        }
    }
}
