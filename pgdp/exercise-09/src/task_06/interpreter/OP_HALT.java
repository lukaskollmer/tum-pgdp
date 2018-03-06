package task_06.interpreter;

/**
 * HALT operation
 *
 * stops program execution by setting the instruction pointer to -1 (soft exit)
 * */
@SuppressWarnings("unused")
@Operation.Opcode(16)
@Operation.HasImmediate(false)
public class OP_HALT extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        program.instructionPointer = -1;
    }
}
