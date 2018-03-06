package task_06.interpreter;

/**
 * MUL operation
 *
 * Pops two values from the stack, multiplies them and pushes the result back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(3)
@Operation.HasImmediate(false)
public class OP_MUL extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        int a = program.stack.pop();
        int b = program.stack.pop();

        program.stack.push(a * b);
    }
}