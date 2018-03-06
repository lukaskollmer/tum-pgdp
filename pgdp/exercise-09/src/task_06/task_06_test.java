package task_06;

import java.util.*;
import java.util.function.BiFunction;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static task_06.Util.f;

import task_06.ast.*;
import task_06.ast.expression.Number;
import task_06.ast.statement.*;
import task_06.ast.expression.*;

import task_06.compiler.*;
import task_06.interpreter.ExecutableProgram;
import task_06.interpreter.Interpreter;


public class task_06_test {


    //
    // HELPERS
    //

    static int random() {
        return random(Integer.MIN_VALUE, Integer.MAX_VALUE - 1);
    }

    static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    // 3 parameter lambda
    @FunctionalInterface
    interface TriFunction<T, U, V, R> {
        R apply(T arg0, U arg1, V arg2);
    }

    // 4 parameter lambda
    @FunctionalInterface
    interface QuadFunction<T, U, V, W, R> {
        R apply(T arg0, U arg1, V arg2, W arg3);
    }


    // compile and run an ast
    int run(Program program) {
        try {
            return run_uncaught(program);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    // compile and run an ast, w/out catching exceptions
    // (we use this for `assertThrows` assertions)
    int run_uncaught(Program program) throws Exception {
        CodeGenerationVisitor codeGenVisitor = new CodeGenerationVisitor();
        program.accept(codeGenVisitor);

        ExecutableProgram executableProgram = new ExecutableProgram(codeGenVisitor.getInstructions());
        executableProgram.compile();

        if (Util.DEBUG) System.out.format("program: %s\n", executableProgram);

        return executableProgram.run();
    }

    //
    // TESTS
    //



    @Test
    public void test_addition() {
        BiFunction<Integer, Integer, Program> makeProgram = (a, b) -> {
            Function add = new Function(
                    "add",
                    Arrays.asList("a", "b"),
                    null,
                    Arrays.asList(
                            new Return(
                                    new Binary(
                                            new Variable("a"),
                                            Binary.Binop.ADDITION,
                                            new Variable("b")
                                    )
                            )
                    )
            );

            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(
                                    new Call("add", new Number(a), new Number(b))
                            )
                    )
            );


            return new Program(main, add);
        };

        for (int i = 0; i < 100_000; i++) {
            int a = random();
            int b = random();

            if (a + b > Integer.MAX_VALUE) continue;

            Assertions.assertEquals(a + b, run(makeProgram.apply(a, b)));
        }
    }


    @Test
    public void test_factorial() {
        java.util.function.Function<Integer, Program> makeProgram = n -> {
            Statement fakRecEnd = new If(
                    new Condition.Comparison(
                            new Variable("n"),
                            Condition.Comparison.Comparator.EQUALS,
                            new Number(0)
                    ),
                    new Return(new Number(1))
            );

            Statement fakRec = new Return(
                    new Binary(
                            new Variable("n"),
                            Binary.Binop.MULTIPLICATION,
                            new Call(
                                    "fak",
                                    new Binary(
                                            new Variable("n"),
                                            Binary.Binop.SUBTRACTION,
                                            new Number(1)
                                    )
                            )
                    )
            );


            Function fak = new Function(
                    "fak",
                    Arrays.asList("n"),
                    null,
                    Arrays.asList(
                            fakRecEnd,
                            fakRec
                    )
            );


            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(new Call("fak", new Number(n)))
                    )
            );

            return new Program(main, fak);
        };

        java.util.function.Function<Integer, Integer> factorial = n -> {
            int retval = 1;

            for (int factor = n; factor >= 2; factor--) {
                retval *= factor;
            }

            return retval;
        };


        // can't go higher than 41 bc that'd cause a stack overflow bc we're limited to a 128 stack frames
        for (int i = 0; i < 41; i++) {
            Assertions.assertEquals((int) factorial.apply(i), run(makeProgram.apply(i)));
        }
    }



    @Test
    public void test_if_else() {
        java.util.function.BiFunction<Integer, Integer, Program> makeProgram = (a, b) -> {

            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new If(
                                    new Condition.Comparison(
                                            new Number(a),
                                            Condition.Comparison.Comparator.LESS,
                                            new Number(b)
                                    ),
                                    new Return(new Number(1)),
                                    new Return(new Number(0))
                            )
                    )
            );

            return new Program(main);
        };


        for (int i = 0; i < 100_000; i++) {
            int a = random();
            int b = random();

            int expected = (a < b) ? 1 : 0;

            Assertions.assertEquals(expected, run(makeProgram.apply(a, b)));
        }
    }


    @Test
    public void test_variables_return_value() {
        TriFunction<Integer, Integer, Integer, Program> makeProgram = (a, b, n) -> {
            Function fn = new Function(
                    "fn",
                    Arrays.asList("arg0", "arg1"),
                    null,
                    Arrays.asList(
                            new Return(new Variable(f("arg%s", n)))
                    )
            );

            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(
                                    new Call(
                                            "fn",
                                            new Number(a),
                                            new Number(b)
                                    )
                            )
                    )
            );

            return new Program(main, fn);
        };


        Assertions.assertEquals(3, run(makeProgram.apply(3, 8, 0)));
        Assertions.assertEquals(8, run(makeProgram.apply(3, 8, 1)));
    }



    @Test
    public void test_addition_multiplication_with_temp_variables() {
        BiFunction<Integer, Integer, Program> makeProgram = (a, b) -> {
            Function add = new Function(
                    "add",
                    Arrays.asList("arg0", "arg1"),
                    Arrays.asList(new Declaration("sum"), new Declaration("mul")),
                    Arrays.asList(
                            new Assignment(
                                    "sum",
                                    new Binary(
                                            new Variable("arg0"),
                                            Binary.Binop.ADDITION,
                                            new Variable("arg1")
                                    )
                            ),
                            new Assignment(
                                    "mul",
                                    new Binary(
                                            new Variable("sum"),
                                            Binary.Binop.MULTIPLICATION,
                                            new Number(10)
                                    )
                            ),
                            new Return(new Variable("mul"))
                    )
            );

            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(new Call("add", new Number(a), new Number(b)))
                    )
            );

            return new Program(main, add);
        };


        for (int i = 0; i < 100_000; i++) {
            int a = random();
            int b = random();

            int expected = (a + b) * 10;

            Assertions.assertEquals(expected, run(makeProgram.apply(a, b)));
        }
    }



    @Test
    public void test_while_loop() {
        java.util.function.Function<Integer, Program> makeProgram = n -> {
            Function count = new Function(
                    "count",
                    Arrays.asList("n"),
                    Arrays.asList(new Declaration("temp", "ret")),
                    Arrays.asList(
                            new While(
                                    new Condition.Comparison(
                                            new Variable("temp"),
                                            Condition.Comparison.Comparator.LESS,
                                            new Variable("n")
                                    ),
                                    new Composite(
                                            // increment temp by 1
                                            new Assignment(
                                                    "temp",
                                                    new Binary(
                                                            new Variable("temp"),
                                                            Binary.Binop.ADDITION,
                                                            new Number(1)
                                                    )
                                            ),
                                            // then increment ret by 2
                                            new Assignment(
                                                    "ret",
                                                    new Binary(
                                                            new Variable("ret"),
                                                            Binary.Binop.ADDITION,
                                                            new Number(2)
                                                    )
                                            )
                                    )
                            ),
                            new Return(new Variable("ret"))
                    )
            );


            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(new Call("count", new Number(n)))
                    )
            );

            return new Program(main, count);
        };

        // this is O(n^2) so we shouldn't make the number too big
        for (int i = 0; i < 2500; i++) {
            Assertions.assertEquals(i * 2, run(makeProgram.apply(i)));
        }
    }



    @Test
    public void test_greatest_common_divisor() {
        BiFunction<Integer, Integer, Program> makeProgram = (a, b) -> {
            Statement ggt_swap = new If(
                    new Condition.Comparison(
                            new Variable("b"),
                            Condition.Comparison.Comparator.GREATER,
                            new Variable("a")
                    ),
                    new Composite(
                            //new Asm("debug"),
                            new Assignment("temp", new Variable("a")),
                            new Assignment("a", new Variable("b")),
                            new Assignment("b", new Variable("temp"))
                            //new Asm("debug")
                    )
            );


            Statement ggt_while = new While(
                    new Condition.Comparison(
                            new Variable("b"),
                            Condition.Comparison.Comparator.NOT_EQUALS,
                            new Number(0)
                    ),
                    new Composite(
                            new Assignment("temp", new Variable("b")),
                            new Assignment(
                                    "b",
                                    new Binary(
                                            new Variable("a"),
                                            Binary.Binop.MODULO,
                                            new Variable("b")
                                    )
                            ),
                            new Assignment("a", new Variable("temp"))
                    )
            );

            Function ggt = new Function(
                    "ggt",
                    Arrays.asList("a", "b"),
                    Arrays.asList(new Declaration("temp")),
                    Arrays.asList(
                            ggt_swap,
                            ggt_while,
                            new Return(new Variable("a"))
                    )
            );


            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(new Call("ggt", new Number(a), new Number(b)))
                    )
            );

            return new Program(main, ggt);
        };


        Assertions.assertEquals(6,   run(makeProgram.apply(12, 18)));
        Assertions.assertEquals(1,   run(makeProgram.apply(16, 175)));
        Assertions.assertEquals(16,  run(makeProgram.apply(144, 160)));
        Assertions.assertEquals(252, run(makeProgram.apply(3780, 3528)));
        Assertions.assertEquals(252, run(makeProgram.apply(3528, 3780)));


        // 32 bit input
        Assertions.assertEquals(504, run(makeProgram.apply(378000, 3528)));
        Assertions.assertEquals(504, run(makeProgram.apply(3528, 378000)));

    }



    @Test
    public void test_unary() {
        BiFunction<Integer, Integer, Program> makeProgram = (a, b) -> {
            // return -(a * b)
            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(
                                    new Unary(
                                            Unary.Unop.MINUS,
                                            new Binary(
                                                    new Number(a),
                                                    Binary.Binop.MULTIPLICATION,
                                                    new Number(b)
                                            )
                                    )
                            )
                    )
            );
            return new Program(main);
        };


        for (int i = 0; i < 100_000; i++) {
            int a = random();
            int b = random();

            Assertions.assertEquals(-(a * b), run(makeProgram.apply(a, b)));
        }
    }


    @Test
    public void test_binary_condition() {
        QuadFunction<Integer, Integer, Integer, Integer, Program> makeProgram = (a, b, c, d) -> {
            Function equal_in_pairs = new Function(
                    "equal_in_pairs",
                    Arrays.asList("a", "b", "c", "d"),
                    null,
                    Arrays.asList(
                            new If(
                                    new Condition.Binary(
                                            new Condition.Comparison(
                                                    new Variable("a"),
                                                    Condition.Comparison.Comparator.EQUALS,
                                                    new Variable("b")
                                            ),
                                            Condition.Binary.Bbinop.AND, // change to OR to require either pair to be equal
                                            new Condition.Comparison(
                                                    new Variable("c"),
                                                    Condition.Comparison.Comparator.EQUALS,
                                                    new Variable("d")
                                            )
                                    ),
                                    new Return(new Number(1)),
                                    new Return(new Number(0))
                            )
                    )
            );

            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(
                                    new Call(
                                            "equal_in_pairs",
                                            new Number(a),
                                            new Number(b),
                                            new Number(c),
                                            new Number(d)
                                    )
                            )
                    )
            );

            return new Program(main, equal_in_pairs);
        };


        for (int i = 0; i < 1000; i++) {
            int a = random(0, 10);
            int b = random(0, 10);
            int c = random(0, 10);
            int d = random(0, 10);

            int expected = ((a == b) && (c == d)) ? 1 : 0;

            Assertions.assertEquals(expected, run(makeProgram.apply(a, b, c, d)));
        }
    }



    @Test
    public void test_true_false() {
        BiFunction<Integer, Integer, Program> makeProgram = (a, b) -> {
            // returns 1 if `a` != `b`
            Function test = new Function(
                    "test",
                    Arrays.asList("a", "b"),
                    null,
                    Arrays.asList(
                            new If(
                                    new Condition.Comparison(
                                            new Condition.Comparison(
                                                    new Variable("a"),
                                                    Condition.Comparison.Comparator.EQUALS,
                                                    new Variable("b")
                                            ),
                                            Condition.Comparison.Comparator.EQUALS,
                                            new Condition.False()
                                    ),
                                    new Return(new Number(1)),
                                    new Return(new Number(0))
                            )
                    )
            );


            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(new Call("test", new Number(a), new Number(b)))
                    )
            );

            return new Program(main, test);
        };


        for (int i = 0; i < 100_000; i++) {
            int a = random();
            int b = random();

            int expected = (a != b) ? 1 : 0;

            Assertions.assertEquals(expected, run(makeProgram.apply(a, b)));
        }
    }


    @Test
    public void test_program_unary() {
        // returns 1 if `a` != `b`
        BiFunction<Integer, Integer, Program> makeProgram = (a, b) -> {
            Function comp = new Function(
                    "comp",
                    Arrays.asList("a", "b"),
                    null,
                    Arrays.asList(
                            new If(
                                    new Condition.Unary(
                                            Condition.Unary.Bunop.NOT,
                                            new Condition.Comparison(
                                                    new Variable("a"),
                                                    Condition.Comparison.Comparator.EQUALS,
                                                    new Variable("b")
                                            )
                                    ),
                                    new Return(new Number(1)),
                                    new Return(new Number(0))
                            )
                    )
            );


            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(new Call("comp", new Number(a), new Number(b)))
                    )
            );


            return new Program(main, comp);
        };


        for (int i = 0; i < 100_000; i++) {
            int a = random();
            int b = random();

            int expected = (a != b) ? 1 : 0;

            Assertions.assertEquals(expected, run(makeProgram.apply(a, b)));
        }
    }


    @Test
    public void test_duplicate_symbols() {
        Function main = new Function(
                "main",
                null,
                null,
                Arrays.asList(
                        new Return(new Number(42))
                )
        );

        Function alsoMain = new Function(
                "main",
                null,
                null,
                Arrays.asList(
                        new Return(new Number(69))
                )
        );

        Program program = new Program(main, alsoMain);

        Assertions.assertThrows(Visitor.Error.class, () -> run_uncaught(program));
    }


    @Test
    public void test_calling_undefined_function() {
        Function main = new Function(
                "main",
                null,
                null,
                Arrays.asList(
                        new Return(new Call("some_function"))
                )
        );

        Program program = new Program(main);

        Assertions.assertThrows(Visitor.Error.class, () -> run_uncaught(program));
    }


    @Test
    public void test_accessing_undefined_variable() {
        Function main = new Function(
                "main",
                null,
                null,
                Arrays.asList(
                        new Return(new Variable("some_variable"))
                )
        );

        Program program = new Program(main);

        Assertions.assertThrows(Visitor.Error.class, () -> run_uncaught(program));
    }


    @Test
    public void test_no_main_function() {
        Function add = new Function(
                "add",
                Arrays.asList("a", "b"),
                null,
                Arrays.asList(
                        new Return(
                                new Binary(
                                        new Variable("a"),
                                        Binary.Binop.ADDITION,
                                        new Variable("b")
                                )
                        )
                )
        );

        Program program = new Program(add);

        Assertions.assertThrows(Visitor.Error.class, () -> run_uncaught(program));
    }


    @Test
    public void test_bitshift_left() {
        BiFunction<Integer, Integer, Program> makeProgram = (a, n) -> {
            Function shift = new Function(
                    "shift",
                    Arrays.asList("a"),
                    null,
                    Arrays.asList(
                            new Asm(f("shl %s", n)),
                            new Return(new Variable("a"))
                    )
            );

            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(new Call("shift", new Number(a)))
                    )
            );

            return new Program(main, shift);
        };


        Assertions.assertEquals(4, run(makeProgram.apply(2, 1)));
        Assertions.assertEquals(8, run(makeProgram.apply(2, 2)));
    }


    @Test
    public void test_32_bit_number_literals() {
        java.util.function.BiFunction<Integer, Integer, Program> makeProgram = (a, b) -> {
            Function add = new Function(
                    "add",
                    Arrays.asList("a", "b"),
                    null,
                    Arrays.asList(
                            new Return(
                                    new Binary(
                                            new Variable("a"),
                                            Binary.Binop.ADDITION,
                                            new Variable("b")
                                    )
                            )
                    )
            );

            Function main = new Function(
                    "main",
                    null,
                    null,
                    Arrays.asList(
                            new Return(new Call("add", new Number(a), new Number(b)))
                    )
            );

            return new Program(main, add);
        };

        for (int i = 0; i < 100_000; i++) {
            int a = random();
            int b = random();

            if (a + b > Integer.MAX_VALUE) continue;

            Assertions.assertEquals(a + b, run(makeProgram.apply(a, b)));
        }
    }



    // test the array based init api
    @Test
    public void test_alternative_api_gcd() {
        Statement ggtSwap =
                new If(
                        new Condition.Comparison(
                                new Variable("b"),
                                Condition.Comparison.Comparator.GREATER,
                                new Variable("a")
                        ),
                        new Composite(
                                new Statement[] {
                                        new Assignment("temp", new Variable("a")),
                                        new Assignment("a", new Variable("b")),
                                        new Assignment("b", new Variable("temp"))
                                }
                        )
                );


        Statement ggtWhile =
                new While(
                        new Condition.Comparison(
                                new Variable("b"),
                                Condition.Comparison.Comparator.NOT_EQUALS,
                                new Number(0)
                        ),
                        new Composite(
                                new Statement[] {
                                        new Assignment("temp", new Variable("b")),
                                        new Assignment("b",
                                                new Binary(
                                                        new Variable("a"),
                                                        Binary.Binop.MODULO,
                                                        new Variable("b")
                                                )
                                        ),
                                        new Assignment("a", new Variable("temp")
                                        )
                                }
                        )
                );

        Function ggt = new Function(
                "ggt",
                new String[] {"a", "b"},
                new Declaration[] {new Declaration(new String[] {"temp"})},
                new Statement[] {ggtSwap, ggtWhile, new Return(new Variable("a"))});


        Function mainFunctionGgt = new Function(
                "main",
                new String[] {},
                new Declaration[] {},
                new Statement[] {
                        new Return(
                                new Call("ggt", new Expression[] {new Number(12), new Number(18)})
                        )
                }
        );

        Program ggtProgram = new Program(new Function[] {mainFunctionGgt, ggt});

        CodeGenerationVisitor cgv = new CodeGenerationVisitor();
        try {
            ggtProgram.accept(cgv);
        } catch (Visitor.Error error) {
            error.printStackTrace();
        }

        int retVal = Interpreter.execute(cgv.getProgram());
        Assertions.assertEquals(6, retVal);

    }


}
