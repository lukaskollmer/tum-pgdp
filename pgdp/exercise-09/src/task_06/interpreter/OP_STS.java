package task_06.interpreter;

/**
 * STS operation
 *
 * Pops a value from the stack and puts it into the frame, at the index specified by the immediate
 * */
@SuppressWarnings("unused")
@Operation.Opcode(7)
@Operation.HasImmediate(true)
public class OP_STS extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        int value = program.stack.pop();
        program.stack.pushFrame(immediate, value);
    }
}
