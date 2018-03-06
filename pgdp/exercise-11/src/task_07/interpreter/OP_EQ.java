package task_07.interpreter;


/**
 * EQ operation
 *
 * Pops two values from the stack, compares them and pushes -1 onto the stack if they are equal (0 if they are not)
 * */
@SuppressWarnings("unused")
@Operation.Opcode(23)
@Operation.HasImmediate(false)
public class OP_EQ extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        int a = program.stack.pop().element1;
        int b = program.stack.pop().element1;

        program.stack.push(StackElement.withElement1(a == b ? -1 : 0));
    }
}

