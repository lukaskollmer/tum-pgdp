package task_09.interpreter;


/**
 * AND operation
 *
 * Pops two values from the stack, performs a bitwise AND, and pushes the result back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(19)
@Operation.HasImmediate(false)
public class OP_AND extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Integer a = (Integer) program.stack.pop();
        Integer b = (Integer) program.stack.pop();

        program.stack.push(a & b);
    }
}

