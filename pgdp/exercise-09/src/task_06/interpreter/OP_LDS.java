package task_06.interpreter;

/**
 * LDS operation
 *
 * Copies the value in the frame at the index specified by the immediate onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(6)
@Operation.HasImmediate(true)
public class OP_LDS extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        int value = program.stack.getFrameElement(immediate);
        program.stack.push(value);
    }
}
