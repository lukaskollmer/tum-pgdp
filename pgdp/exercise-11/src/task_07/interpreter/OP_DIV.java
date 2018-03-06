package task_07.interpreter;


/**
 * DIV operation
 *
 * Pops two values from the stack, divides them and pushes the result back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(18)
@Operation.HasImmediate(false)
public class OP_DIV extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        int a = program.stack.pop().element1;
        int b = program.stack.pop().element1;

        program.stack.push(StackElement.withElement1(a / b));
    }
}