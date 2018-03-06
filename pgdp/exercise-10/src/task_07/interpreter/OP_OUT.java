package task_07.interpreter;

/**
 * OUT operation
 *
 * Pops a value from the stack and prints it to stdout
 * */
@SuppressWarnings("unused")
@Operation.Opcode(13)
@Operation.HasImmediate(false)
public class OP_OUT extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        int value = program.stack.pop();
        System.out.format("[OUT] %s\n", value);
    }
}
