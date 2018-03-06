package task_09.interpreter;

import java.util.Arrays;
import java.util.List;

/**
 * Sole purpose of this class is to provide the interface expected by the unit test
 * */
public class Interpreter {

    public static int[] parse(String sourceCode) {
        List<String> instructions = Arrays.asList(sourceCode.split("\n"));

        ExecutableProgram program = new ExecutableProgram(instructions);

        try {
            program.compile();
        } catch (ExecutableProgram.CompileException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return program.getOpcodes();
    }

    public static int execute(int[] rawInstructions) {
        ExecutableProgram program = new ExecutableProgram(rawInstructions);

        return program.run();
    }


    public static String programToString(int[] rawInstructions) {
        return new ExecutableProgram(rawInstructions).toString();
    }
}
