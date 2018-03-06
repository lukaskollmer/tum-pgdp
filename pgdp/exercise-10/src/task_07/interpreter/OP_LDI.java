package task_07.interpreter;

/**
 * LDI operation
 *
 * Pushes the value in the immediate onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(5)
@Operation.HasImmediate(true)
public class OP_LDI extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        immediate <<= 16;
        immediate >>= 16;
        program.stack.push(immediate);
    }
}
