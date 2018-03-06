package task_09;

import task_09.ast.Program;
import task_09.compiler.CodeGenerationVisitor;
import task_09.compiler.Compiler;
import task_09.interpreter.ExecutableProgram;

import java.util.concurrent.ThreadLocalRandom;


// some helper functions for our unit tests

public class LKTestUtils {


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
