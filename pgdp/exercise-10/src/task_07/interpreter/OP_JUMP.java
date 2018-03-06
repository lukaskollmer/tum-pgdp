package task_07.interpreter;

/**
 * JUMP operation
 *
 * Moves the instruction pointer to the address specified by the immediate, if the first value on the stack is -1
 * */
@SuppressWarnings("unused")
@Operation.Opcode(8)
@Operation.HasImmediate(true)
public class OP_JUMP extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        if (program.stack.pop() == -1) {
            program.instructionPointer = immediate;
        }
    }
}
