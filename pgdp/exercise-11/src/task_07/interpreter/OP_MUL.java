package task_07.interpreter;

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
        int a = program.stack.pop().element1;
        int b = program.stack.pop().element1;

        program.stack.push(StackElement.withElement1(a * b));
    }
}