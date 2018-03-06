package task_09;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task_09.ast.Declaration;
import task_09.ast.Function;
import task_09.ast.Program;
import task_09.ast.Variable;
import task_09.ast.expression.*;
import task_09.ast.expression.Number;
import task_09.ast.statement.*;
import task_09.compiler.CodeGenerationVisitor;
import task_09.compiler.Compiler;
import task_09.compiler.Visitor;
import task_09.interpreter.ExecutableProgram;
import task_09.interpreter.Interpreter;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static task_09.LKTestUtils.*;
import static task_09.Util.*;

class tests_interpreter_ast_codegen {

    static {
        MultilineStringLiteral.directory = "./src/";
    }


    @Test
    void test_automatic_reference_counting() {
        String code = MultilineStringLiteral.read(
/*

int[] getArr(int size, int value) {
    int[] array1;
    int[] array2;
    int index;
    array1 = new int[size];
    array2 = new int[size];

    while(index < size) {
        array1[index] = value;
        array2[index] = value * 2;
        index = index + 1;
    }

    return array2;
}

int number() {
    int[] arr;

    arr = new int[5];
    arr[4] = 128;

    return arr[4];
}


int main() {
    int[] array1;
    int[] array2;
    int[] array3;
    int[] array4;
    int[] array5;

    array1 = new int[2];
    array2 = new int[2];
    array3 = new int[2];
    array4 = new int[2];

    array1[0] = 1;
    array1[1] = 1;

    array2[0] = 2;
    array2[1] = 2;

    array3[0] = 3;
    array3[1] = 3;

    array4[0] = 4;
    array4[1] = 4;

    array5 = getArr(5, 12);

    return array5[4];
}
*/
        );

        __test_arc_heap_is_empty(code);
    }


    private void __test_arc_heap_is_empty(String code) {
        try {
            Compiler compiler = new Compiler(code);
            ExecutableProgram executableProgram = compiler.compileToExecutableProgram();

            executableProgram.run();
            assertEquals(true, executableProgram.getHeap().isEmpty());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void test_automatic_reference_counting_with_classes() {
        String code = MultilineStringLiteral.read(
/*
class Counter {
    int count;

    Counter() {}

    void increment() {
        count = count + 1;
        return 0;
    }

    int getCount() {
        return count;
    }
}

int main() {
    Counter counter;
    counter = new Counter();

    counter.increment();
    counter.increment();
    counter.increment();
    counter.increment();
    counter.increment();
    return counter.getCount();
}
*/
        );

        assertEquals(5, run(code));

        __test_arc_heap_is_empty(code);

    }


    @Test
    void test_array_access_out_of_bounds() {
        String code = MultilineStringLiteral.read(
/*

int main() {
    int[] array;
    array = new int[12];
    array = new int[5];

    return array[7];
}

*/
        );

        assertThrows(RuntimeException.class, () -> run(code));
    }



    @Test
    void test_arrays_basic() {
        String code = MultilineStringLiteral.read(
/*
int main() {
    int[] arr;
    int i, n, sum;
    n = 5;
    arr = new int[n];
    i = 0;
    while(i < n) {
        arr[i] = 2*i;
        i = i + 1;
    }
    sum = 0;
    i = 0;
    while(i < n) {
        sum = sum + arr[i];
        i = i + 1;
    }
    return sum;
}
*/
        );

        assertEquals(20, run(code));
    }

    @Test
    void test_arrays_function() {
        String code = MultilineStringLiteral.read(
/*
int sum(int[] arr, int n) {
    int i, sum;
    sum = 0;
    i = 0;
    while(i < n) {
        sum = sum + arr[i];
        i = i + 1;
    }
    return sum;
}

int main() {
    int[] arr;
    int i, n, x, sum;
    n = 5;
    arr = new int[n];
    i = 0;
    while(i < n) {
        arr[i] = 2*i;
        i = i + 1;
    }
    return sum(arr, n);
}
*/
        );
        assertEquals(20, run(code));
    }

    @Test
    void test_arrays_function_returns_array() {
        String code = MultilineStringLiteral.read(
/*
int[] generateArray(int n) {
    int[] arr;
    int i;
    arr = new int[n];
    i = 0;
    while(i < n) {
        arr[i] = 2*i;
        i = i + 1;
    }
    return arr;
}

int sum(int[] arr, int n) {
    int i, sum;
    sum = 0;
    i = 0;
    while(i < n) {
        sum = sum + arr[i];
        i = i + 1;
    }
    return sum;
}

int main() {
    int[] arr;
    int n;
    n = 5;
    arr = generateArray(n);
    return sum(arr, n);
}
*/
        );

        int[] assembly = Compiler.compile(code);
        int retVal = Interpreter.execute(assembly);
        assertEquals(20, retVal);
    }

    @Test
    void test_array_length() {
        String code = MultilineStringLiteral.read(
/*
int[] generateArray() {
    int[] arr;
    int i;
    arr = new int[6];
    i = 0;
    while(i < length(arr)) {
        arr[i] = 2*i;
        i = i + 1;
    }
    return arr;
}

int sum(int[] arr) {
    int i, sum;
    sum = 0;
    i = 0;
    while(i < length(arr)) {
        sum = sum + arr[i];
        i = i + 1;
    }
    return sum;
}

int main() {
    int[] arr;
    arr = generateArray();
    return sum(arr);
}
*/
        );

        int[] assembly = Compiler.compile(code);
        int retVal = Interpreter.execute(assembly);
        assertEquals(30, retVal);
    }

    @Test
    void test_palindrome() {
        String code = MultilineStringLiteral.read(
/*
int isPalindrome(int n) {
    int[] digits;
    int numberOfDigits, t, i, notMatching, digit;

    numberOfDigits = 0;
    t = n;
    while (t != 0) {
        numberOfDigits = numberOfDigits + 1;
        t = t / 10;
    }

    digits = new int[numberOfDigits];

    i = 0;
    while (n != 0) {
        digit = n % 10;
        digits[i] = digit;
        n = n / 10;
        i = i + 1;
    }

    notMatching = 0;
    i = 0;
    while (i < numberOfDigits / 2) {
        if (digits[i] != digits[(numberOfDigits - i) - 1])
            notMatching = notMatching + 1;
        i = i + 1;
    }

    if (notMatching == 0)
        return 1;
    else
        return 0;
}

int main() {
    int n;
    n = 0;
    n = n + isPalindrome(4224);
    n = n + isPalindrome(10);
    n = n + isPalindrome(99);
    n = n + isPalindrome(123321);
    n = n + isPalindrome(19910);
    n = n + isPalindrome(0990);
    n = n + isPalindrome(111111);
    n = n + isPalindrome(1112111);
    return n;
}
*/
        );

        int[] assembly = Compiler.compile(code);
        int retVal = Interpreter.execute(assembly);
        assertEquals(5, retVal);
    }

    @Test
    void test_ggt() {
        String code = MultilineStringLiteral.read(
/*
int ggt(int a, int b) {
    int temp;
    if(b > a) {
        temp = a;
        a = b;
        b = temp;
    }

    while(a != 0) {
        temp = a;
        a = a % b;
        b = temp;
    }
    return b;
}

int main() {
    int a, b, r;
    a = 3528;
    b = 3780;
    r = ggt(a, b);
    return r;
}
*/
        );
        int[] assembly = Compiler.compile(code);
        int retVal = Interpreter.execute(assembly);
        assertEquals(252, retVal);
    }

    @Test
    void test_fak() {
        String code = MultilineStringLiteral.read(
/*
int fak(int n) {
    if(n == 0)
        return 1;
    return n*fak(n - 1);
}

int main() {
    return fak(6);
}
*/
        );
        int[] assembly = Compiler.compile(code);
        int retVal = Interpreter.execute(assembly);
        assertEquals(720, retVal);
    }

    @Test
    void test_prim() {
        String code = MultilineStringLiteral.read(
/*
int prim(int n) {
    int divisors, i;
    divisors = 0;

    i = 2;
    while (i < n) {
        if (n % i == 0)
            divisors = divisors + 1;
        i = i + 1;
    }

    if (divisors == 0 && n >= 2) {
        return 1;
    } else {
        return 0;
    }
}

int main() {
    int prims;
    prims = 0;
    prims = prims + prim(997);
    prims = prims + prim(120);
    prims = prims + prim(887);
    prims = prims + prim(21);
    prims = prims + prim(379);
    prims = prims + prim(380);
    prims = prims + prim(757);
    prims = prims + prim(449);
    prims = prims + prim(5251);
    return prims;
}
*/
        );
        int[] assembly = Compiler.compile(code);
        int retVal = Interpreter.execute(assembly);
        assertEquals(5, retVal);
    }

    @Test()
    void test_invalid_call() {
        String code = MultilineStringLiteral.read(
/*
int ggt(int a, int b) {
    return b;
}

int main() {
    int a, b, r;
    a = 3528;
    b = 3780;
    r = ggt(a, b, a);
    return r;
}
*/
        );

        assertThrows(RuntimeException.class, () -> Compiler.compile(code));
    }

    @Test()
    void test_undefined_variable() {
        String code = MultilineStringLiteral.read(
/*
int ggt(int a, int b) {
    return c;
}

int main() {
    int a, b, r;
    a = 3528;
    b = 3780;
    r = ggt(a, b);
    return r;
}
*/
        );

        assertThrows(RuntimeException.class, () -> Compiler.compile(code));
    }

    @Test()
    void test_double_definition() {
        String code = MultilineStringLiteral.read(
/*
int ggt(int a, int b) {
    int a;
    return b;
}

int main() {
    int a, b, r;
    a = 3528;
    b = 3780;
    r = ggt(a, b);
    return r;
}
*/
        );

        assertThrows(RuntimeException.class, () -> Compiler.compile(code));
    }


    @Test
    void test_array_access_after_function_call() {
        String code = MultilineStringLiteral.read(
/*
int[] getArray(int size) {
    int index;
    int[] array;

    array = new int[size];

    index = 0;
    while (index < size) {
        array[index] = 2 * index;
        index = index + 1;
    }

    return array;
}

int main() {
    return getArray(5)[4];
}
*/
        );

        assertEquals(8, run(code));

    }


    @Test
    void test_array_length_2() {
        String code = MultilineStringLiteral.read(
/*

int main() {
    int[] array;
    int[] array2;
    int[] array3;

    array = new int[7];
    array2 = new int[20];
    array3 = new int[50];

    return length(array);
}
*/
        );

        //Util.DEBUG = true;

        assertEquals(7, run(code));
    }



    //
    // Tests from previous tasks
    //

    @Test
    void test_addition() {

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
            //Assertions.assertEquals(a + b, run(generateCode.apply(a, b)));
            assertEquals((a + b), run(generateCode.apply(a, b)));
        }
    }


    @Test
    void test_factorial() {

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
            assertEquals((int) factorial.apply(i), run(generateCode.apply(i)));
        }
    }



    @Test
    void test_if_else() {

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

            assertEquals(expected, run(generateCode.apply(a, b)));
        }
    }


    @Test
    void test_variables_return_value() {

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

        assertEquals(3, run(generateCode.apply(3, 8, 0)));
        assertEquals(8, run(generateCode.apply(3, 8, 1)));
    }



    @Test
    void test_addition_multiplication_with_temp_variables() {

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
            assertEquals(expected, run(generateCode.apply(a, b)));
        }
    }



    @Test
    void test_while_loop() {

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
            assertEquals(i * 2, run(generateCode.apply(i)));
        }
    }



    @Test
    void test_greatest_common_divisor() {

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


        assertEquals(6,   run(generateCode.apply(12, 18)));
        assertEquals(1,   run(generateCode.apply(16, 175)));
        assertEquals(16,  run(generateCode.apply(144, 160)));
        assertEquals(252, run(generateCode.apply(3780, 3528)));
        assertEquals(252, run(generateCode.apply(3528, 3780)));


        // 32 bit input
        assertEquals(504, run(generateCode.apply(378000, 3528)));
        assertEquals(504, run(generateCode.apply(3528, 378000)));

    }



    @Test
    void test_unary() {
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

            assertEquals(-(a * b), run(generateCode.apply(a, b)));
        }
    }


    @Test
    void test_binary_condition() {

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

            assertEquals(expected, run(generateCode.apply(a, b, c, d)));
        }
    }



    @Test
    void test_bunop() {
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

            assertEquals(expected, run(generateCode.apply(a, b)));
        }
    }


    @Test
    void test_duplicate_symbols() {

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

        assertThrows(Visitor.Error.class, () -> run_uncaught(code));
    }


    @Test
    void test_calling_undefined_function() {

        String code = MultilineStringLiteral.read(
/*
int main() {
    return fn();
}
*/
        );

        assertThrows(Visitor.Error.class, () -> run_uncaught(code));
    }


    @Test
    void test_accessing_undefined_variable() {
        String code = MultilineStringLiteral.read(
/*
int main() {
    return x;
}
*/
        );

        assertThrows(Visitor.Error.class, () -> run_uncaught(code));
    }


    @Test
    void test_no_main_function() {
        String code = MultilineStringLiteral.read(
/*
int add() {
    return x;
}
*/
        );

        assertThrows(Visitor.Error.class, () -> run_uncaught(code));
    }


    @Test
    void test_duplicate_symbol_in_local_and_global_namespace() {
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

        assertEquals(5, run(code));
    }







    //
    // Recursion / Tail Call Optimization
    //


    private static int STACK_SIZE = 32;

    private static String factorial_tail_recursive_code = MultilineStringLiteral.read(
/*
int fak(int n, int acc) {
    if(n == 0)
        return acc;
    return fak(n - 1, acc*n);
}
int main() {
    return fak(12, 1);
}
*/
    );


    private static String factorial_not_tail_recursive_code = MultilineStringLiteral.read(
/*
int fak(int n) {
    if(n == 0)
        return 1;
    return n*fak(n - 1);
}
int main() {
    return fak(12);
}
*/
    );


    // note: in the `run_code{_uncaught}` function calls below, the last parameter specifies whether optimizations should be disabled


    @Test
    void test_factorial_not_tail_recursive_not_optimized() {
        assertThrows(RuntimeException.class, () -> run_uncaught(factorial_not_tail_recursive_code, STACK_SIZE, true));
    }


    @Test
    void test_factorial_not_tail_recursive_optimized() {
        assertThrows(RuntimeException.class, () ->  run(factorial_not_tail_recursive_code, STACK_SIZE, false));
    }


    @Test
    void test_factorial_tail_recursive_not_optimized() {
        assertThrows(RuntimeException.class, () -> run_uncaught(factorial_tail_recursive_code, STACK_SIZE, true));
    }

    @Test
    void test_factorial_tail_recursive_optimized() {
        assertEquals(479001600, run(factorial_tail_recursive_code, STACK_SIZE, false));
    }









    //
    // AST tests
    //

    @Test
    void test_ast_addition() {
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
                    Collections.emptyList(),
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
    void test_ast_factorial() {
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
                    Collections.emptyList(),
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
    void test_ast_if_else() {
        java.util.function.BiFunction<Integer, Integer, Program> makeProgram = (a, b) -> {

            Function main = new Function(
                    "main",
                    Collections.emptyList(),
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
    void test_ast_variables_return_value() {
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
                    Collections.emptyList(),
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
    void test_ast_addition_multiplication_with_temp_variables() {
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
                    Collections.emptyList(),
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
    void test_ast_while_loop() {
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
                    Collections.emptyList(),
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
    void test_ast_greatest_common_divisor() {
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
                    Collections.emptyList(),
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
    void test_ast_unary() {
        BiFunction<Integer, Integer, Program> makeProgram = (a, b) -> {
            // return -(a * b)
            Function main = new Function(
                    "main",
                    Collections.emptyList(),
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
    void test_ast_binary_condition() {
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
                    Collections.emptyList(),
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
    void test_ast_true_false() {
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
                    Collections.emptyList(),
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
    void test_ast_program_unary() {
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
                    Collections.emptyList(),
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
    void test_ast_duplicate_symbols() {
        Function main = new Function(
                "main",
                Collections.emptyList(),
                null,
                Arrays.asList(
                        new Return(new Number(42))
                )
        );

        Function alsoMain = new Function(
                "main",
                Collections.emptyList(),
                null,
                Arrays.asList(
                        new Return(new Number(69))
                )
        );

        Program program = new Program(main, alsoMain);

        Assertions.assertThrows(Visitor.Error.class, () -> run_uncaught(program));
    }


    @Test
    void test_ast_calling_undefined_function() {
        Function main = new Function(
                "main",
                Collections.emptyList(),
                null,
                Arrays.asList(
                        new Return(new Call("some_function"))
                )
        );

        Program program = new Program(main);

        Assertions.assertThrows(Visitor.Error.class, () -> run_uncaught(program));
    }


    @Test
    void test_ast_accessing_undefined_variable() {
        Function main = new Function(
                "main",
                Collections.emptyList(),
                null,
                Arrays.asList(
                        new Return(new Variable("some_variable"))
                )
        );

        Program program = new Program(main);

        Assertions.assertThrows(Visitor.Error.class, () -> run_uncaught(program));
    }


    @Test
    void test_ast_no_main_function() {
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
    void test_ast_bitshift_left() {
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
                    Collections.emptyList(),
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
    void test_ast_32_bit_number_literals() {
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
                    Collections.emptyList(),
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
    void test_ast_alternative_api_gcd() {
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



    //
    // OO tests
    //


    @Test
    void test_oo_object_array() {
        String code = MultilineStringLiteral.read(
/*

class Foo {
    int a;

    Foo(int x) {
        write(x);
        a = x;
    }

    int getA() {
        return a;
    }
}

int main() {
    Foo[] foos;
    Foo foo;
    int sum, i;
    foos = new Foo[5];
    i = 0;
    while(i < length(foos)) {
        foos[i] = new Foo((2*i) + 1);
        i = i + 1;
    }
    i = length(foos);
    sum = 0;
    while(i > 0) {
        foo = foos[i - 1];
        sum = sum + foo.getA();
        i = i - 1;
    }
    return sum;
}
*/
        );

        assertEquals(25, run(code));
    }

    @Test
    void test_oo_visitor() {
        String code = MultilineStringLiteral.read(
/*
class Grundflaeche {
    Grundflaeche() {
    }

    int accept(Visitor visitor) {
        return 0;
    }
}

class Visitor {
    Visitor() {
    }

    int visitQ(Quadrat quadrat) {
        return 0;
    }

    int visitR(Rechteck rechteck) {
        return 0;
    }
}

class FlaechenVisitor extends Visitor {
    int flaeche;

    FlaechenVisitor() {
        super.Visitor();
    }

    int getFlaeche() {
        return flaeche;
    }

    int visitQ(Quadrat quadrat) {
        flaeche = quadrat.getLaenge() * quadrat.getLaenge();
        return 0;
    }

    int visitR(Rechteck rechteck) {
        flaeche = rechteck.getBreite() * rechteck.getLaenge();
        return 0;
    }
}

class Rechteck extends Grundflaeche {
    int breite;

    int laenge;

    Rechteck(int breiteP, int laengeP) {
        super.Grundflaeche();
        breite = breiteP;
        laenge = laengeP;
    }

    int getBreite() {
        return breite;
    }

    int getLaenge() {
        return laenge;
    }

    int accept(Visitor visitor) {
        visitor.visitR(this);
        return 0;
    }
}

class Quadrat extends Grundflaeche {
    int laenge;

    Quadrat(int laengeP) {
        super.Grundflaeche();
        laenge = laengeP;
    }

    int getLaenge() {
        return laenge;
    }

    int accept(Visitor visitor) {
        visitor.visitQ(this);
        return 0;
    }
}

int flaeche(Grundflaeche g) {
    FlaechenVisitor visitor;
    visitor = new FlaechenVisitor();
    g.accept(visitor);
    return visitor.getFlaeche();
}

int main() {
    Grundflaeche g2, g3, g6;
    int sum;
    g2 = new Rechteck(2, 3);
    g3 = new Rechteck(4, 4);
    g6 = new Quadrat(8);
    sum = flaeche(g2);
    sum = sum + flaeche(g3);
    sum = sum + flaeche(g6);
    return sum;
}
*/
        );

        assertEquals(86, run(code));
    }

    @Test
    void test_oo_heap() {
        String code = MultilineStringLiteral.read(
/*
class Heap {

  int[] heap;
  int[] sorted;

  Heap(int size) {
    heap = new int[size];
  }

  int[] getHeap() {
    return heap;
  }

  int[] getSorted() {
    return sorted;
  }

  int sort() {
    int i;
    sorted = new int[length(heap)];
    this.buildHeap();
    i = 0;
    while (i < length(heap)) {
      sorted[i] = heap[0];
      heap[0] = heap[length(heap) - (1 + i)];
      heap[length(heap) - (1 + i)] = -1;
      this.down(0, length(heap) - (2 + i));
      i = i + 1;
    }
    return 0;
  }


  int swap(int first, int second) {
    int temp;
    temp = heap[first];
    heap[first] = heap[second];
    heap[second] = temp;
    return 0;
  }

  int daughter(int index) {
    return (2 * index) + 1;
  }

  int son(int index) {
    return (2 * index) + 2;
  }

  int buildHeap() {
    int index;
    index = length(heap) / 2;
    while(index >= 0) {
      this.down(index, length(heap) - 1);
      index = index - 1;
    }
    return 0;
  }

  int down(int index, int stop) {
    int mindex;
    if (index > stop) {
      return 0;
    }
    mindex = this.minIndex(index, this.daughter(index), this.son(index), stop);
    if (mindex != index)
      if (heap[mindex] < heap[index]) {
        this.swap(index, mindex);
        this.down(mindex, stop);
      }
    return 0;
  }

  int minIndex(int i1, int i2, int i3, int stop) {
    if(i2 > stop) {
      if (i3 > stop)
        return i1;
      else
        return i3;
    }
    if (heap[i1] <= heap[i2]) {
      if (i3 > stop)
        return i1;
      if (heap[i1] <= heap[i3])
        return i1;
      else
        return i3;
    } else {
      if(i3 > stop)
        return i2;
      if (heap[i2] <= heap[i3])
        return i2;
      else
        return i3;
    }
  }
}

int main() {
  Heap heap;
  int[] arr;
  int i, sum;
  heap = new Heap(10);
  arr = heap.getHeap();
  arr[0] = 4;
  arr[1] = 1;
  arr[2] = 9;
  arr[3] = 7;
  arr[4] = 8;
  arr[5] = -10;
  arr[6] = 50;
  arr[7] = 2;
  arr[8] = 15;
  arr[9] = -3;
  heap.sort();
  arr = heap.getSorted();
  if(arr[0] != -10) return 99;
  if(arr[1] != -3) return 99;
  if(arr[2] != 1) return 99;
  if(arr[3] != 2) return 99;
  if(arr[4] != 4) return 99;
  if(arr[5] != 7) return 99;
  if(arr[6] != 8) return 99;
  if(arr[7] != 9) return 99;
  if(arr[8] != 15) return 99;
  if(arr[9] != 50) return 99;
  return 42;
}
*/
        );
        assertEquals(42, run(code));
    }


    @Test
    void test_oo_super() {
        String code =
                MultilineStringLiteral.read(
/*
class Foo {
  Foo() {
  }

  int x() {
    return 20;
  }
}

class Bar extends Foo {
  Bar() {
    super.Foo();
  }

  int x() {
    return super.x();
  }
}

int main() {
  Bar b;
  b = new Bar();
  return b.x();
}
*/
);

        //Util.DEBUG = true;

        assertEquals(20, run(code));
    }

    @Test
    void test_oo_inheritance_2() {
        String code = MultilineStringLiteral.read(
/*
class Bar {
  Bar() {
  }

  int x() {
    return 22;
  }
}

class Foo extends Bar {
  Foo() {
    super.Bar();
  }

  int x() {
    return 99;
  }

  int y() {
    return this.x() + 2;
  }
}

int main() {
  Foo foo1, foo2;
  Bar bar1, bar2;
  foo1 = new Foo();
  foo2 = foo1;
  bar1 = new Bar();
  bar2 = foo1;
  return foo1.x() + foo2.x() + bar1.x() + bar2.x();
}
*/
                );

        assertEquals(319, run(code));
    }

    @Test
    void test_oo_inheritance_1() {
        String code = MultilineStringLiteral.read(
/*
class Bar {
  Bar() {
  }

  int x() {
    return 22;
  }
}

class Foo extends Bar {
  Foo() {
    super.Bar();
  }

  int y() {
    return this.x() + 2;
  }
}

int main() {
  Foo foo;
  foo = new Foo();
  return foo.y() + foo.x();
}
*/
        );

        //Util.DEBUG = true;
        assertEquals(46, run(code));
    }

    @Test
    void test_oo_class_basics_1() {
        String code = MultilineStringLiteral.read(
/*
class Foo {
  int a;

  Foo() {
    a = 100;
  }

  int x() {
    return a;
  }
}

int main() {
  Foo foo;
  foo = new Foo();
  return foo.x();
}
*/
        );
        assertEquals(100, run(code));
    }

    @Test
    void test_oo_class_basics_2() {
        String code = MultilineStringLiteral.read(
/*
class Foo {
  int a;

  Foo() {
    a = 100;
  }

  int y() {
    return a + a;
  }

  int x() {
    return 1 + this.y() + this.y();
  }
}

int main() {
  Foo foo;
  foo = new Foo();
  return foo.x();
}
*/
        );
        assertEquals(401, run(code));
    }

    @Test
    void test_oo_class_basics_3() {
        String code = MultilineStringLiteral.read(
/*
class Bar {
  int a;

  Bar() {
    a = 7;
  }

  int test(Foo f) {
    return f.x() + a;
  }
}

class Foo {
  int a;

  Foo(int k) {
    a = k;
  }

  int y() {
    return a + a;
  }

  int x() {
    return 1 + this.y() + this.y();
  }
}

int main() {
  Foo foo;
  Bar bar;
  foo = new Foo(100);
  bar = new Bar();
  return bar.test(foo);
}
*/
                );
        //Parser.DEBUG = true;
        assertEquals(408, run(code));
    }


    @Test
    void test_oo_forward_declaration() {
        String code = MultilineStringLiteral.read(
/*
class X {
    X() {}

    int doSomething() {
        B obj;
        obj = new B();
        return obj.getNumber();
    }
}


class A {
    A() {}

    int getNumber() {
        return 12;
    }
}


class B extends A {
    B() {
        super.A();
    }

    int getNumber() {
        return 2 * super.getNumber();
    }
}



int main() {
    X x;
    x = new X();
    return x.doSomething();
}
*/
        );

        assertEquals(24, run(code));
    }
}
