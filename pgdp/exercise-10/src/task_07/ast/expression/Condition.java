package task_07.ast.expression;

import task_07.compiler.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static task_07.Util.f;


public interface Condition extends Expression {

    class True implements Condition {
        @Override
        public void accept(Visitor visitor) throws Visitor.Error {
            visitor.visit(this);
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
        public String toString() {
            return "<Condition.False>";
        }
    }



    class Binary implements Condition {
        public enum Bbinop {
            AND,
            OR
        }

        private final Condition lhs;
        private final Bbinop operator;
        private final Condition rhs;

        public Binary(Condition lhs, Bbinop operator, Condition rhs) {
            this.lhs = lhs;
            this.operator = operator;
            this.rhs = rhs;
        }


        public Condition getLhs() {
            return lhs;
        }

        public Bbinop getOperator() {
            return operator;
        }

        public Condition getRhs() {
            return rhs;
        }


        @Override
        public void accept(Visitor visitor) throws Visitor.Error {
            visitor.visit(this);
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
            EQUALS("EQ"),
            NOT_EQUALS("EQ", "NOT"),
            LESS_EQUAL("LE"),
            LESS("LT"),
            GREATER_EQUAL("LT", "NOT"),
            GREATER("LE", "NOT");

            public final List<String> instructions;

            Comparator(String... instructions) {
                this.instructions = Arrays.asList(instructions);
            }
        }



        private final Expression lhs;
        private final Comparator comparator;
        private final Expression rhs;


        public Comparison(Expression lhs, Comparator comparator, Expression rhs) {
            this.lhs = lhs;
            this.comparator = comparator;
            this.rhs = rhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Comparator getComparator() {
            return comparator;
        }

        public Expression getRhs() {
            return rhs;
        }


        @Override
        public void accept(Visitor visitor) throws Visitor.Error {
            visitor.visit(this);
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

        private final Bunop operator;
        private final Condition condition;

        public Unary(Bunop operator, Condition condition) {
            this.operator = operator;
            this.condition = condition;
        }


        public Bunop getOperator() {
            return operator;
        }

        public Condition getCondition() {
            return condition;
        }


        @Override
        public void accept(Visitor visitor) throws Visitor.Error {
            visitor.visit(this);
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
