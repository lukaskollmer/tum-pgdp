package task_09.interpreter;


/**
 * OR operation
 *
 * Pops two values from the stack, performs a bitwise OR, and pushes the result back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(20)
@Operation.HasImmediate(false)
public class OP_OR extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Integer a = (Integer) program.stack.pop();
        Integer b = (Integer) program.stack.pop();

        program.stack.push(a | b);
    }
}