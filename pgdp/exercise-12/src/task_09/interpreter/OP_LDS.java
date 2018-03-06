package task_09.interpreter;

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
        Object element = program.stack.getFrameElement(immediate);
        program.stack.push(element);
    }
}
