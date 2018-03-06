package task_09.interpreter;

import task_09.arc.Trackable;

/**
 * RELEASE operation
 *
 * custom operation that pops an element off the stack and decreases the retain count by 1
 * */
@SuppressWarnings("unused")
@Operation.Opcode(97)
@Operation.HasImmediate(false)
public class OP_RELEASE extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Object element = program.stack.pop();
        Trackable.releaseIfPossible(element);
    }
}
