package task_07;

import org.junit.jupiter.api.Test;

import task_07.ast.*;
import task_07.ast.expression.Number;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formatter;
import task_07.ast.statement.*;
import task_07.ast.expression.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class task_06_tests {

    @Test
    void testNumber() {
        Expression expression = new Number(5);

        assertEquals("5", Formatter.format(expression));
    }


    @Test
    void testExpression1() {
        Expression exp = new Binary(
                new Binary(
                        new Number(99), Binary.Binop.ADDITION, new Number(11)
                ),
                Binary.Binop.SUBTRACTION,
                new Binary(
                        new Variable("a"), Binary.Binop.ADDITION, new Number(1)
                )
        );

        assertEquals("99 + 11 - (a + 1)", Formatter.format(exp));
    }

    @Test
    void testExpression2() {
        Expression exp = new Binary(
                new Binary(
                        new Number(99), Binary.Binop.ADDITION, new Number(11)
                ),
                Binary.Binop.ADDITION,
                new Binary(
                        new Variable("a"), Binary.Binop.ADDITION, new Number(1)
                )
        );
        assertEquals("99 + 11 + a + 1", Formatter.format(exp));
    }

    @Test
    void testExpression3() {
        Expression exp = new Binary(new Binary(new Number(99), Binary.Binop.ADDITION, new Number(11)), Binary.Binop.ADDITION,
                new Binary(new Variable("a"), Binary.Binop.MULTIPLICATION, new Number(1)));
        FormatVisitor visitor = new FormatVisitor();
        exp.accept(visitor);
        assertEquals("99 + 11 + a * 1", Formatter.format(exp));
    }

    @Test
    void testExpression4() {
        Expression exp = new Unary(Unary.Unop.MINUS, new Binary(new Number(99), Binary.Binop.ADDITION, new Number(11)));
        FormatVisitor visitor = new FormatVisitor();
        exp.accept(visitor);
        assertEquals("-(99 + 11)", Formatter.format(exp));
    }

    @Test
    void testExpression5() {
        Expression exp = new Unary(Unary.Unop.MINUS,
                new Binary(new Number(99), Binary.Binop.MULTIPLICATION, new Number(11)));
        FormatVisitor visitor = new FormatVisitor();
        exp.accept(visitor);
        assertEquals("-99 * 11", Formatter.format(exp));
    }

    @Test
    void testExpression6() {
        Expression exp = new ArrayElementGetter(new ArrayCreation(new Number(99)), new Number(3));
        FormatVisitor visitor = new FormatVisitor();
        exp.accept(visitor);
        assertEquals("(new int[99])[3]", Formatter.format(exp));
    }

    @Test
    void testExpression7() {
        Expression exp = new Binary(new Binary(new Number(99), Binary.Binop.DIVISION, new Number(11)),
                Binary.Binop.DIVISION,
                new Binary(new Variable("a"), Binary.Binop.DIVISION, new Number(1)));
        FormatVisitor visitor = new FormatVisitor();
        exp.accept(visitor);
        assertEquals("99 / 11 / (a / 1)", Formatter.format(exp));
    }

    @Test
    void testExpression8() {
        Expression exp = new Binary(
                new Binary(new Number(99), Binary.Binop.DIVISION,
                        new Call("hugo", new Expression[] {new Number(1), new Variable("b")})),
                Binary.Binop.DIVISION,
                new Binary(new Variable("a"), Binary.Binop.DIVISION, new Number(1)));
        FormatVisitor visitor = new FormatVisitor();
        exp.accept(visitor);
        assertEquals("99 / hugo(1, b) / (a / 1)", Formatter.format(exp));
    }

    @Test
    void testExpression9() {
        Expression exp = new Binary(new Binary(new Number(99), Binary.Binop.ADDITION, new Number(11)),
                Binary.Binop.MULTIPLICATION, new Binary(new Variable("a"), Binary.Binop.ADDITION, new Number(1)));
        FormatVisitor visitor = new FormatVisitor();
        exp.accept(visitor);
        assertEquals("(99 + 11) * (a + 1)", Formatter.format(exp));
    }

    @Test
    void testExpression10() {
        Expression exp =
                new Binary(new Binary(new Number(99), Binary.Binop.MULTIPLICATION, new Number(11)),
                        Binary.Binop.MULTIPLICATION, new Binary(new Variable("a"), Binary.Binop.ADDITION, new Number(1)));
        FormatVisitor visitor = new FormatVisitor();
        exp.accept(visitor);
        assertEquals("99 * 11 * (a + 1)", Formatter.format(exp));
    }

    @Test
    void testExpression11() {
        Expression exp = new Binary(new Binary(new Number(99), Binary.Binop.SUBTRACTION, new Number(11)),
                Binary.Binop.MULTIPLICATION,
                new Binary(new Variable("a"), Binary.Binop.MULTIPLICATION, new Number(1)));
        FormatVisitor visitor = new FormatVisitor();
        exp.accept(visitor);
        assertEquals("(99 - 11) * a * 1", Formatter.format(exp));
    }

    @Test
    void testCondition1() {
        Condition cond = new Condition.Binary(new Condition.True(), Condition.Binary.Bbinop.AND,
                new Condition.Binary(new Condition.False(), Condition.Binary.Bbinop.AND, new Condition.True()));
        FormatVisitor visitor = new FormatVisitor();
        cond.accept(visitor);
        assertEquals("true && false && true", Formatter.format(cond));
    }

    @Test
    void testCondition2() {
        Condition cond = new Condition.Binary(new Condition.True(), Condition.Binary.Bbinop.AND,
                new Condition.Binary(new Condition.False(), Condition.Binary.Bbinop.OR, new Condition.True()));
        FormatVisitor visitor = new FormatVisitor();
        cond.accept(visitor);
        assertEquals("true && (false || true)", Formatter.format(cond));
    }

    @Test
    void testCondition3() {
        Condition cond =
                new Condition.Binary(new Condition.Comparison(new Number(2), Condition.Comparison.Comparator.GREATER, new Variable("a")),
                        Condition.Binary.Bbinop.OR, new Condition.Binary(new Condition.False(), Condition.Binary.Bbinop.AND, new Condition.True()));
        FormatVisitor visitor = new FormatVisitor();
        cond.accept(visitor);
        assertEquals("2 > a || false && true", Formatter.format(cond));
    }

    @Test
    void testCondition4() {
        Condition cond =
                new Condition.Unary(Condition.Unary.Bunop.NOT, new Condition.Comparison(new Number(1), Condition.Comparison.Comparator.EQUALS, new Number(2)));
        FormatVisitor visitor = new FormatVisitor();
        cond.accept(visitor);
        assertEquals("!(1 == 2)", Formatter.format(cond));
    }

    @Test
    void testCondition5() {
        Condition cond = new Condition.Unary(Condition.Unary.Bunop.NOT,
                new Condition.Binary(new Condition.Comparison(new Number(2), Condition.Comparison.Comparator.GREATER, new Variable("a")),
                        Condition.Binary.Bbinop.OR, new Condition.Binary(new Condition.False(), Condition.Binary.Bbinop.AND, new Condition.True())));
        FormatVisitor visitor = new FormatVisitor();
        cond.accept(visitor);
        assertEquals("!(2 > a || false && true)", Formatter.format(cond));
    }

}
