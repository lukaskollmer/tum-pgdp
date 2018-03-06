package task_09.interpreter;

/**
 * ADD operation
 *
 * Pops two values from the stack, adds them and pushes the result back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(1)
@Operation.HasImmediate(false)
public class OP_ADD extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Integer a = (Integer) program.stack.pop();
        Integer b = (Integer) program.stack.pop();

        program.stack.push(a + b);
    }
}
