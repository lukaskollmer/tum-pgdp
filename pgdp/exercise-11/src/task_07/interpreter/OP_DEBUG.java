package task_07.interpreter;

/**
 * DEBUG operation
 *
 * custom operation that prints the current contents of the stack (including stack- and framepointer) to stdout
 * */
@SuppressWarnings("unused")
@Operation.Opcode(99)
@Operation.HasImmediate(true)
public class OP_DEBUG extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        if (immediate == 1) {
            int topElement = program.stack.pop().element1;
            System.out.format("[DEBUG] stack.top: %s\n", topElement);
            program.stack.push(StackElement.withElement1(topElement));
        } else {
            System.out.format("[DEBUG] stack: %s\n", program.stack);
        }
    }
}
