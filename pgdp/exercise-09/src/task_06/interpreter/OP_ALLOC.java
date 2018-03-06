package task_06.interpreter;

/**
 * ALLOC operation
 *
 * Reserves space on the stack for local variables
 * */
@SuppressWarnings("unused")
@Operation.Opcode(17)
@Operation.HasImmediate(true)
public class OP_ALLOC extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        for (int i = 0; i < immediate; i++) {
            program.stack.push(0);
        }
    }
}
