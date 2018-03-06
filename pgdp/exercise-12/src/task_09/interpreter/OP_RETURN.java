package task_09.interpreter;

import task_09.arc.Trackable;

/**
 * RETURN operation
 *
 * 1. pops the return value from the stack
 * 2. pops the local variables from the stack (#variables set via the immediate)
 * 3. pops the previous instruction pointer from the stack and restores it
 * 4. pops the previous frame pointer from the stack and restores it
 * 5. pushes the return value back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(15)
@Operation.HasImmediate(true)
public class OP_RETURN extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Object retval = program.stack.pop();

        // todo potential problem here:
        // if we return an object from the local scope, we increase it's retain count unnecessarily
        // this might be the problem w/ that one example (`return makeArray(1, 2)[5]`) FIXME
        Trackable.retainIfPossible(retval);

        for (int i = 0; i < immediate; i++) {
            Object element = program.stack.pop();

            Trackable.releaseIfPossible(element);
        }

        program.instructionPointer = (Integer) program.stack.pop();
        program.stack.framePointer = (Integer) program.stack.pop();

        program.stack.push(retval);
    }
}
