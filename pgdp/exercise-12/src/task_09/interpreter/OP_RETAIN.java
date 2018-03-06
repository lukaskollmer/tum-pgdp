package task_09.interpreter;

import task_09.arc.Trackable;

/**
 * RETAIN operation
 *
 * custom operation that pops an element off the stack and increases the retain count by 1
 * */
@SuppressWarnings("unused")
@Operation.Opcode(98)
@Operation.HasImmediate(false)
public class OP_RETAIN extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Object element = program.stack.pop();
        Trackable.retainIfPossible(element);
    }
}
