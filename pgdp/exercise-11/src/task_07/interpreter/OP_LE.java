package task_07.interpreter;


/**
 * LE operation
 *
 * Pops two values from the stack, compares them and pushes -1 onto the stack
 * if the first one is <= the second one (0 if it isn't)
 * */
@SuppressWarnings("unused")
@Operation.Opcode(25)
@Operation.HasImmediate(false)
public class OP_LE extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        int a = program.stack.pop().element1;
        int b = program.stack.pop().element1;

        program.stack.push(StackElement.withElement1(a <= b ? -1 : 0));
    }
}