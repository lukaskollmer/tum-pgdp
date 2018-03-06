package task_07;

import task_07.ast.Program;
import task_07.compiler.CodeGenerationVisitor;
import task_07.compiler.Compiler;
import task_07.interpreter.ExecutableProgram;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ThreadLocalRandom;


// some helper functions for our unit tests

public class LKTestUtils {

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
    static int run(Program program) {
        try {
            return run_uncaught(program);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // compile and run an ast, w/out catching exceptions
    // (we use this for `assertThrows` assertions)
    static int run_uncaught(Program program) throws Exception {
        CodeGenerationVisitor codeGenVisitor = new CodeGenerationVisitor();
        program.accept(codeGenVisitor);

        ExecutableProgram executableProgram = new ExecutableProgram(codeGenVisitor.getInstructions());
        executableProgram.compile();

        if (Util.DEBUG) System.out.format("program: %s\n", executableProgram);

        return executableProgram.run();
    }


    static int run(String code) {
        return run(code, 128, false);
    }

    static int run(String code, int stackSize, boolean disableOptimization) {
        try {
            return run_uncaught(code, stackSize, disableOptimization);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static int run_uncaught(String code) throws Exception {
        return run_uncaught(code, 128, false);
    }


    static int run_uncaught(String code, int stackSize, boolean disableOptimization) throws Exception {
        Compiler compiler = new Compiler(code);
        if (disableOptimization) {
            compiler.disableOptimization();
        }

        ExecutableProgram executableProgram = compiler.compileToExecutableProgram();

        executableProgram.setStackSize(stackSize);

        if (Util.DEBUG) System.out.format("program: %s\n", executableProgram);

        return executableProgram.run();

    }
}
