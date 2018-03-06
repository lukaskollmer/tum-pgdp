package task_07;

import task_07.ast.*;
import task_07.ast.expression.*;
import task_07.ast.expression.Number;
import task_07.ast.formatter.FormatVisitor;
import task_07.ast.formatter.Formatter;
import task_07.ast.statement.*;

import java.util.Arrays;

public class Main_Task_07 {
    public static void main(String... args) {


        // copied from one of the unit tests

        Function doSomthing = new Function(
                "doSomething",
                Arrays.asList("a", "b", "c"),
                Arrays.asList(new Declaration("d", "e", "f"), new Declaration("g", "h", "i")),
                Arrays.asList(
                        new Return(
                                new ArrayElementGetter(
                                        new Variable("i"),
                                        new Binary(
                                                new Variable("g"),
                                                Binary.Binop.ADDITION,
                                                new Variable("h")
                                        )
                                )
                        )
                )
        );


        Function main = new Function(
                "main",
                null,
                null,
                Arrays.asList(
                        new Assignment("array", new ArrayCreation(new Number(12))),
                        new ArrayElementSetter(
                                "array",
                                new Binary(
                                        new Variable("i"), Binary.Binop.ADDITION, new Number(1)
                                ),
                                new Call("mul", new Variable("i"))
                        ),
                        new If(
                                new Condition.Comparison(new Number(5), Condition.Comparison.Comparator.GREATER, new Number(4)),
                                new Composite(
                                new Return(
                                        new Call(
                                                "doSomething",
                                                new Number(1),
                                                new Binary(new Number(2), Binary.Binop.MODULO, new Number(5))
                                        )
                                )),
                                new Composite(new Return(new Number(0)))
                        ),
                        new Asm("ldi 1"),
                        new Composite(
                                new Assignment("var1", new Variable("var2")),
                                new Read("input"),
                                new Write(new Variable("output")),
                                new While(
                                        new Condition.Comparison(new Number(1), Condition.Comparison.Comparator.EQUALS, new Number(0)),
                                        new Return(new Number(0)) // todo this is not correctly indented!!!
                                )
                        ),

                        new If(
                                new Condition.Binary(
                                        new Condition.Comparison(new Number(5), Condition.Comparison.Comparator.LESS_EQUAL, new Variable("b")),
                                        Condition.Binary.Bbinop.AND,
                                        new Condition.Unary(Condition.Unary.Bunop.NOT, new Condition.Comparison(new Number(12), Condition.Comparison.Comparator.EQUALS, new Number(12)))
                                ),
                                new Assignment("var", new Number(1)),
                                new Assignment("var", new Number(0))
                        ),

                        new While(
                                new Condition.True(),
                                new Return(new Variable("a"))
                        ),
                        new Assignment("len", new ArrayLength_inline(new Variable("array"))),
                        new Assignment("val", new Unary(Unary.Unop.MINUS, new Variable("val")))
                )
        );

        Program program = new Program(main, doSomthing);


        System.out.format("CODE:\n");
        System.out.format("%s\n", Formatter.format(program));

        //System.out.format("%s\n", Formatter.format(new Assignment("temp", new Number(42))));
    }
}
