package task_07;

import java.util.function.BiFunction;

import task_07.compiler.*;
import task_07.compiler.Compiler;
import task_07.interpreter.Interpreter;


/*
*
* this file contains some tests to check that MiniJava code is translated into the correct instructions
*
* please note that this DOES NOT use junit. instead, just run the main function and it'll print its progress to stdout
*
* also: you should either be in `src` or src's parent folder when running this.
* this is important for our multiline string literals to work properly
* */


public class task_07_test extends Test {



    public static void main(String... args) {
        if (!System.getProperty("user.dir").endsWith("/src")) {
            MultilineStringLiteral.directory = "./src/";
        }

        Test.runAll(task_07_test.class);
    }


    //
    // TESTS
    //



    @SuppressWarnings("unused")
    public static void test_addition() {

        BiFunction<Integer, Integer, String> generateCode = (a, b) -> {
            return MultilineStringLiteral.read(
                    new MultilineStringLiteral.Template("$PARAM_0$", Integer.toString(a)),
                    new MultilineStringLiteral.Template("$PARAM_1$", Integer.toString(b))
/*
int add(int a, int b) {
    return a + b;
}

int main() {
    int x, y;
    x = $PARAM_0$;
    y = $PARAM_1$;

    return add(x, y);
}
*/
            );
        };

        for (int i = 0; i < 100; i++) {
            int a = random();
            int b = random();

            if (a + b > Integer.MAX_VALUE) continue;
            //Assertions.assertEquals(a + b, run_code(generateCode.apply(a, b)));
            assertEqual((a + b), run_code(generateCode.apply(a, b)));
        }
    }


    @SuppressWarnings("unused")
    public static void test_factorial() {

        java.util.function.Function<Integer, String> generateCode = n -> {
            return MultilineStringLiteral.read(
                    new MultilineStringLiteral.Template("$PARAM_0$", Integer.toString(n))
/*
int factorial(int n) {
    if (n == 0) {
        return 1;
    } else {
        return n * factorial(n - 1);
    }
}

int main() {
    return factorial($PARAM_0$);
}
*/
            );
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
            assertEqual((int) factorial.apply(i), run_code(generateCode.apply(i)));
        }
    }



    @SuppressWarnings("unused")
    public static void test_if_else() {

        BiFunction<Integer, Integer, String> generateCode = (a, b) -> {
            return MultilineStringLiteral.read(
                    new MultilineStringLiteral.Template("$PARAM_0$", Integer.toString(a)),
                    new MultilineStringLiteral.Template("$PARAM_1$", Integer.toString(b))
/*
int main() {
    if ($PARAM_0$ < $PARAM_1$) {
        return 1;
    } else {
        return 0;
    }
}
*/
            );
        };


        for (int i = 0; i < 5_000; i++) {
            int a = random();
            int b = random();

            int expected = (a < b) ? 1 : 0;

            assertEqual(expected, run_code(generateCode.apply(a, b)));
        }
    }


    @SuppressWarnings("unused")
    public static void test_variables_return_value() {

        TriFunction<Integer, Integer, Integer, String> generateCode = (a, b, n) -> {
            return MultilineStringLiteral.read(
                    new MultilineStringLiteral.Template("$PARAM_0$", Integer.toString(a)),
                    new MultilineStringLiteral.Template("$PARAM_1$", Integer.toString(b)),
                    new MultilineStringLiteral.Template("$RET_IDX$", Integer.toString(n))
/*
int fn(int arg0, int arg1) {
    return arg$RET_IDX$;
}

int main() {
    return fn($PARAM_0$, $PARAM_1$);
}
*/
            );
        };

        assertEqual(3, run_code(generateCode.apply(3, 8, 0)));
        assertEqual(8, run_code(generateCode.apply(3, 8, 1)));
    }



    @SuppressWarnings("unused")
    public static void test_addition_multiplication_with_temp_variables() {

        BiFunction<Integer, Integer, String> generateCode = (a, b) -> {
            return MultilineStringLiteral.read(
                    new MultilineStringLiteral.Template("$PARAM_0$", Integer.toString(a)),
                    new MultilineStringLiteral.Template("$PARAM_1$", Integer.toString(b))
/*
int addMul(int arg0, int arg1) {
    int sum, mul;

    sum = arg0 + arg1;
    mul = sum * 10;

    return mul;
}

int main() {
    int a, b;
    a = $PARAM_0$;
    b = $PARAM_1$;
    return addMul(a, b);
}
*/
            );
        };



        for (int i = 0; i < 1000; i++) {
            int a = random();
            int b = random();

            int expected = (a + b) * 10;
            assertEqual(expected, run_code(generateCode.apply(a, b)));
        }
    }



    @SuppressWarnings("unused")
    public static void test_while_loop() {

        java.util.function.Function<Integer, String> generateCode = n -> {
            return MultilineStringLiteral.read(
                    new MultilineStringLiteral.Template("$PARAM_0$", Integer.toString(n))
/*
int count(int n) {
    int temp, ret;

    while (temp < n) {
        temp = temp + 1;
        ret = ret + 2;
    }

    return ret;
}

int main() {
    return count($PARAM_0$);
}
*/
            );
        };


        // this is O(n^2) so we shouldn't make the number too big
        for (int i = 0; i < 1000; i++) {
            assertEqual(i * 2, run_code(generateCode.apply(i)));
        }
    }



    @SuppressWarnings("unused")
    public static void test_greatest_common_divisor() {

        BiFunction<Integer, Integer, String> generateCode = (a, b) -> {
            return MultilineStringLiteral.read(
                    new MultilineStringLiteral.Template("$PARAM_0$", Integer.toString(a)),
                    new MultilineStringLiteral.Template("$PARAM_1$", Integer.toString(b))
/*
int gcd(int a, int b) {
    int temp;

    if (b > a) {
        temp = b;
        b = a;
        a = temp;
    }
    while (b != 0) {
        temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}

int main() {
    int a, b;
    a = $PARAM_0$;
    b = $PARAM_1$;

    return gcd(a, b);
}
*/
            );
        };


        assertEqual(6,   run_code(generateCode.apply(12, 18)));
        assertEqual(1,   run_code(generateCode.apply(16, 175)));
        assertEqual(16,  run_code(generateCode.apply(144, 160)));
        assertEqual(252, run_code(generateCode.apply(3780, 3528)));
        assertEqual(252, run_code(generateCode.apply(3528, 3780)));


        // 32 bit input
        assertEqual(504, run_code(generateCode.apply(378000, 3528)));
        assertEqual(504, run_code(generateCode.apply(3528, 378000)));

    }



    @SuppressWarnings("unused")
    public static void test_unary() {
        BiFunction<Integer, Integer, String> generateCode = (a, b) -> {
            return MultilineStringLiteral.read(
                    new MultilineStringLiteral.Template("$PARAM_0$", Integer.toString(a)),
                    new MultilineStringLiteral.Template("$PARAM_1$", Integer.toString(b))
/*
int main() {
    int a, b;
    a = $PARAM_0$;
    b = $PARAM_1$;

    return -(a * b);
}
*/
            );
        };


        for (int i = 0; i < 1000; i++) {
            int a = random();
            int b = random();

            assertEqual(-(a * b), run_code(generateCode.apply(a, b)));
        }
    }


    @SuppressWarnings("unused")
    public static void test_binary_condition() {

        QuadFunction<Integer, Integer, Integer, Integer, String> generateCode = (a, b, c, d) -> {
            return MultilineStringLiteral.read(
                    new MultilineStringLiteral.Template("$PARAM_0$", Integer.toString(a)),
                    new MultilineStringLiteral.Template("$PARAM_1$", Integer.toString(b)),
                    new MultilineStringLiteral.Template("$PARAM_2$", Integer.toString(c)),
                    new MultilineStringLiteral.Template("$PARAM_3$", Integer.toString(d))
/*
int equalInPairs(int a, int b, int c, int d) {
    if (a == b && c == d) {
        return 1;
    } else {
        return 0;
    }
}

int main() {
    int a, b, c, d;

    a = $PARAM_0$;
    b = $PARAM_1$;
    c = $PARAM_2$;
    d = $PARAM_3$;

    return equalInPairs(a, b, c, d);
}
*/
            );
        };

        for (int i = 0; i < 1000; i++) {
            int a = random(0, 10);
            int b = random(0, 10);
            int c = random(0, 10);
            int d = random(0, 10);

            int expected = ((a == b) && (c == d)) ? 1 : 0;

            assertEqual(expected, run_code(generateCode.apply(a, b, c, d)));
        }
    }



    @SuppressWarnings("unused")
    public static void test_bunop() {
        BiFunction<Integer, Integer, String> generateCode = (a, b) -> {
            return MultilineStringLiteral.read(
                    new MultilineStringLiteral.Template("$PARAM_0$", Integer.toString(a)),
                    new MultilineStringLiteral.Template("$PARAM_1$", Integer.toString(b))
/*
int test(int a, int b) {
    if (!(a == b)) {
        return 1;
    } else {
        return 0;
    }
}


int main() {
    int a, b;

    a = $PARAM_0$;
    b = $PARAM_1$;

    return test(a, b);
}
*/
            );
        };


        for (int i = 0; i < 1000; i++) {
            int a = random();
            int b = random();

            int expected = (a != b) ? 1 : 0;

            assertEqual(expected, run_code(generateCode.apply(a, b)));
        }
    }


    @SuppressWarnings("unused")
    public static void test_duplicate_symbols() {

        String code = MultilineStringLiteral.read(
/*
int main() {
    return 0;
}

int main() {
    return 1;
}
*/
        );

        assertThrows(Visitor.Error.class, () -> run_code_uncaught(code));
    }


    @SuppressWarnings("unused")
    public static void test_calling_undefined_function() {

        String code = MultilineStringLiteral.read(
/*
int main() {
    return fn();
}
*/
        );

        assertThrows(Visitor.Error.class, () -> run_code_uncaught(code));
    }


    @SuppressWarnings("unused")
    public static void test_accessing_undefined_variable() {
        String code = MultilineStringLiteral.read(
/*
int main() {
    return x;
}
*/
        );

        assertThrows(Visitor.Error.class, () -> run_code_uncaught(code));
    }


    @SuppressWarnings("unused")
    public static void test_no_main_function() {
        String code = MultilineStringLiteral.read(
/*
int add() {
    return x;
}
*/
        );

        assertThrows(Visitor.Error.class, () -> run_code_uncaught(code));
    }


    @SuppressWarnings("unused")
    public static void test_ggt() {
        String ggtCode = "int ggt(int a, int b) {\n" +
                "  int temp;\n" +
                "  if(b > a) {\n" +
                "    temp = a;\n" +
                "    a = b;\n" +
                "    b = temp;\n" +
                "  }\n" +
                "  while(a != 0) {\n" +
                "   temp = a;\n" +
                "   a = a % b;\n" +
                "   b = temp;\n" +
                "  }\n" +
                "  return b;\n" +
                "}\n" +
                "\n" +
                "int main() {\n" +
                "  int a, b, r;\n" +
                "  a = 3528;\n" +
                "  b = 3780;\n" +
                "  r = ggt(a, b);\n" +
                "  return r;\n" +
                "}";
        int[] assembly = Compiler.compile(ggtCode);
        int retVal = Interpreter.execute(assembly);
        assertEqual(252, retVal);
    }

    @SuppressWarnings("unused")
    public static void test_fak() {
        String fakCode = "int fak(int n) {\n" +
                "  if(n == 0)\n" +
                "    return 1;\n" +
                "  return n*fak(n - 1);\n" +
                "}\n" +
                "\n" +
                "int main() {\n" +
                "  return fak(6);\n" +
                "}\n";
        int[] assembly = Compiler.compile(fakCode);
        int retVal = Interpreter.execute(assembly);
        assertEqual(720, retVal);
    }

    @SuppressWarnings("unused")
    public static void test_prim() {
        String primTestCode = "int prim(int n) {\n" +
                "  int divisors, i;\n" +
                "  divisors = 0;\n" +
                "  \n" +
                "  i = 2;\n" +
                "  while (i < n) {\n" +
                "    if (n % i == 0)\n"  +
                //"    {\n" +
                "      divisors = divisors + 1;" +
                //"    }\n" +
                "    i = i + 1;\n" +
                "  }\n" +
                "  \n" +
                "  if (divisors == 0 && n >= 2) {\n" +
                "    return 1;\n" +
                "  } else {\n" +
                "    return 0;\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "int main() {\n" +
                "  int prims;\n" +
                "  prims = 0;\n" +
                "  prims = prims + prim(997);\n" +
                "  prims = prims + prim(120);\n" +
                "  prims = prims + prim(887);\n" +
                "  prims = prims + prim(21);\n" +
                "  prims = prims + prim(379);\n" +
                "  prims = prims + prim(380);\n" +
                "  prims = prims + prim(757);\n" +
                "  prims = prims + prim(449);\n" +
                "  prims = prims + prim(5251);\n" +
                "  return prims;\n" +
                "}";

        //Parser.DEBUG = Util.DEBUG = true;

        int[] assembly = Compiler.compile(primTestCode);
        int retVal = Interpreter.execute(assembly);
        assertEqual(5, retVal);
    }


    @SuppressWarnings("unused")
    public static void test_invalid_call() {
        String invalidCode = "int ggt(int a, int b) {\n" +
                "  return b;\n" +
                "}\n" +
                "\n" +
                "int main() {\n" +
                "  int a, b, r;\n" +
                "  a = 3528;\n" +
                "  b = 3780;\n" +
                "  r = ggt(a, b, a);\n" +
                "  return r;\n" +
                "}";
        Compiler.compile(invalidCode);
    }


    @SuppressWarnings("unused")
    public static void test_undefined_variable() {
        String invalidCode = "int ggt(int a, int b) {\n" +
                "  return c;\n" +
                "}\n" +
                "\n" +
                "int main() {\n" +
                "  int a, b, r;\n" +
                "  a = 3528;\n" +
                "  b = 3780;\n" +
                "  r = ggt(a, b);\n" +
                "  return r;\n" +
                "}";

        assertThrows(RuntimeException.class, () -> Compiler.compile(invalidCode));
    }


    @SuppressWarnings("unused")
    public static void test_double_definition() {
        String invalidCode = "int ggt(int a, int b) {\n" +
                "  int a;\n" +
                "  return b;\n" +
                "}\n" +
                "\n" +
                "int main() {\n" +
                "  int a, b, r;\n" +
                "  a = 3528;\n" +
                "  b = 3780;\n" +
                "  r = ggt(a, b);\n" +
                "  return r;\n" +
                "}";

        assertThrows(RuntimeException.class, () -> Compiler.compile(invalidCode));
    }


    @SuppressWarnings("unused")
    public static void test_duplicate_symbol_in_local_and_global_namespace() {
        String code = MultilineStringLiteral.read(
/*
int add() {
    return -1;
}

int main() {
    int add;
    add = 5;
    return add;
}
*/
        );


        assertThrows(RuntimeException.class, () -> Compiler.compile(code));
    }



}
