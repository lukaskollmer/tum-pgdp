package task_09.interpreter;

/**
 * MOD operation
 *
 * Pops two values from the stack, calculates the modulo and pushes the result back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(4)
@Operation.HasImmediate(false)
public class OP_MOD extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Integer a = (Integer) program.stack.pop();
        Integer b = (Integer) program.stack.pop();

        program.stack.push(a % b);
    }
}
