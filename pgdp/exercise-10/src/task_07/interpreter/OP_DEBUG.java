package task_07.interpreter;

/**
 * DEBUG operation
 *
 * custom operation that prints the current contents of the stack (including stack- and framepointer) to stdout
 * */
@SuppressWarnings("unused")
@Operation.Opcode(99)
@Operation.HasImmediate(false)
public class OP_DEBUG extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        System.out.format("[DEBUG] stack: %s\n", program.stack);
    }
}
