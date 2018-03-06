package task_09.interpreter;

/**
 * SUB operation
 *
 * Pops two values from the stack, subtracts them and pushes the result back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(2)
@Operation.HasImmediate(false)
public class OP_SUB extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Integer a = (Integer) program.stack.pop();
        Integer b = (Integer) program.stack.pop();

        program.stack.push(a - b);
    }
}
