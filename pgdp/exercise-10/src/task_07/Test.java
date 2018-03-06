package task_07;

import task_07.ast.Program;
import task_07.compiler.CodeGenerationVisitor;
import task_07.compiler.Compiler;
import task_07.interpreter.ExecutableProgram;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ThreadLocalRandom;

public class Test {
    public static class Colors {
        static final String ANSI_RESET = "\u001B[0m";
        static final String ANSI_RED   = "\u001B[31m";
        static final String ANSI_GREEN = "\u001B[32m";
    }

    private static String lastTestName = "";


    private static <T> void _handleAssertionResult(String name, boolean success, T expected, T actual) {
        String symbol = success
                ? Colors.ANSI_GREEN + "✔" + Colors.ANSI_RESET
                : Colors.ANSI_RED   + "✖" + Colors.ANSI_RESET;


        //if (lastTestName == null) {
        //    lastTestName = name;
        //}

        //System.out.print("\r");

        if (!lastTestName.equals(name)) {
            System.out.format("%s %s\n", symbol, name);
            lastTestName = name;
        }

        if (!success) {
            System.out.format("\n\n");
            System.out.print(Colors.ANSI_RED + "test failed: " + Colors.ANSI_RESET + name + "\n");

            System.out.format("expected: %s\n", expected);
            System.out.format("actual:   %s\n", actual);

            System.out.format("\n");
            System.exit(1);
        }

    }


    private static String getCallingFunction() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }


    static <T> void assertEqual(T expected, T value) {
        _handleAssertionResult(getCallingFunction(), value.equals(expected), expected, value);
    }

    @FunctionalInterface
    public interface Executable {
        void execute() throws Throwable;
    }

    static <T extends Throwable> void assertThrows(Class<T> cls, Executable block) {
        try {
            block.execute();
        } catch (Throwable exception) {
            _handleAssertionResult(getCallingFunction(), exception.getClass().equals(cls), cls, exception.getClass());
            return;
        }

        _handleAssertionResult(getCallingFunction(), false, cls, null);
    }


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
            throw new RuntimeException(e);
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


    static int run_code(String code) {
        return run_code(code, 128, false);
    }

    static int run_code(String code, int stackSize, boolean disableOptimization) {
        try {
            return run_code_uncaught(code, stackSize, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static int run_code_uncaught(String code) throws Exception {
        return run_code_uncaught(code, 128, false);
    }


    static int run_code_uncaught(String code, int stackSize, boolean disableOptimization) throws Exception {
        Compiler compiler = new Compiler(code);
        if (disableOptimization) {
            compiler.disableOptimization();
        }

        ExecutableProgram executableProgram = compiler.compileToExecutableProgram();

        executableProgram.setStackSize(stackSize);

        if (Util.DEBUG) System.out.format("program: %s\n", executableProgram);

        return executableProgram.run();

    }




    public static void runAll(Class<?> cls) {
        System.out.format("Running tests...\n");
        System.out.format("This may take up to a minute...\n");

        for (Method m : cls.getDeclaredMethods()) {
            if (m.getName().startsWith("test_") && Modifier.isStatic(m.getModifiers())) {
                try {
                    m.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("\n" + Colors.ANSI_GREEN + "All tests passed" + Colors.ANSI_RESET);
    }
}
