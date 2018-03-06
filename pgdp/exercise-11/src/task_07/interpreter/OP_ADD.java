package task_07.interpreter;

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
        int a = program.stack.pop().element1;
        int b = program.stack.pop().element1;

        program.stack.push(StackElement.withElement1(a + b));
    }
}
