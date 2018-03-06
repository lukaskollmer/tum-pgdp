package task_09.interpreter;


/**
 * LT operation
 *
 * Pops two values from the stack, compares them and pushes -1 onto the stack
 * if the first one is < than the second one (0 if it isn't)
 * */
@SuppressWarnings("unused")
@Operation.Opcode(24)
@Operation.HasImmediate(false)
public class OP_LT extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Integer a = (Integer) program.stack.pop();
        Integer b = (Integer) program.stack.pop();

        program.stack.push(a < b ? -1 : 0);
    }
}