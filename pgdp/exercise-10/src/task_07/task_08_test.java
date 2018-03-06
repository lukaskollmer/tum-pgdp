package task_07;

public class task_08_test extends Test {

    // we have to adjust the directory in the static initializer to make sure
    // it runs before the static variables are initialized
    static {
        if (!System.getProperty("user.dir").endsWith("/src")) {
            MultilineStringLiteral.directory = "./src/";
        }
    }

    private static int STACK_SIZE = 32;

    public static void main(String... args) {
        Test.runAll(task_08_test.class);
    }


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


    @SuppressWarnings("unused")
    public static void test_factorial_not_tail_recursive_not_optimized() {
        assertThrows(RuntimeException.class, () -> run_code_uncaught(factorial_not_tail_recursive_code, STACK_SIZE, true));
    }


    @SuppressWarnings("unused")
    public static void test_factorial_not_tail_recursive_optimized() {
        assertThrows(RuntimeException.class, () ->  run_code(factorial_not_tail_recursive_code, STACK_SIZE, false));
    }


    @SuppressWarnings("unused")
    public static void test_factorial_tail_recursive_not_optimized() {
        assertThrows(RuntimeException.class, () -> run_code_uncaught(factorial_tail_recursive_code, STACK_SIZE, true));
    }

    @SuppressWarnings("unused")
    public static void test_factorial_tail_recursive_optimized() {
        assertEqual(479001600, run_code(factorial_tail_recursive_code, STACK_SIZE, false));
    }

}
