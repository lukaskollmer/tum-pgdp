package task_07.interpreter;

import task_07.arc.Trackable;

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
        StackElement retval = program.stack.pop();

        if (retval.element2 instanceof Trackable) {
            ((Trackable)retval.element2).retain();
        }

        for (int i = 0; i < immediate; i++) {
            StackElement element = program.stack.pop();

            if (element.element2 instanceof Trackable) {
                ((Trackable)element.element2).release();
            }
        }

        program.instructionPointer = program.stack.pop().element1;
        program.stack.framePointer = program.stack.pop().element1;

        program.stack.push(retval);
    }
}
