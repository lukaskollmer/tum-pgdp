package task_06.interpreter;


/**
 * NOT operation
 *
 * Pops a value from the stack, performs a bitwise NOT, and pushes the result back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(21)
@Operation.HasImmediate(false)
public class OP_NOT extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        int a = program.stack.pop();

        program.stack.push(~a);
    }
}
