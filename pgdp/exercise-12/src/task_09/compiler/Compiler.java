package task_09.compiler;

import task_09.ast.Parser;
import task_09.ast.Program;
import task_09.interpreter.ExecutableProgram;

import java.util.List;

public class Compiler {

    private final String code;

    private boolean shouldOptimize = true;

    public Compiler(String code) {
        this.code = code;
    }

    // Disable tail recursive call optimizations
    public void disableOptimization() {
        shouldOptimize = false;
    }


    // Generate an AST describing the source code
    public Program parseToAST() {
        Parser tokenizer = new Parser(this.code);

        return tokenizer.parse();
    }


    // Compile the source code to an ExecutableProgram
    public ExecutableProgram compileToExecutableProgram() throws Exception {
        CodeGenerationVisitor codeGen = new CodeGenerationVisitor();
        parseToAST().accept(codeGen);

        List<String> instructions = shouldOptimize
                ? Optimizer.optimizeInstructions(codeGen.getInstructions())
                : codeGen.getInstructions();

        ExecutableProgram executableProgram = new ExecutableProgram(instructions);
        executableProgram.compile();

        return executableProgram;
    }



    public int[] compileToInstructions() {
        try {
            return compileToExecutableProgram().getOpcodes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // The interface expected by the unit test we got as part of the instructions

    public static int[] compile(String code) {
        return new Compiler(code).compileToInstructions();
    }
}
