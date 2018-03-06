package task_07.interpreter;

import task_07.arc.Trackable;

/**
 * STS operation
 *
 * Pops a value from the stack and puts it into the frame, at the index specified by the immediate
 * */
@SuppressWarnings("unused")
@Operation.Opcode(7)
@Operation.HasImmediate(true)
public class OP_STS extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        StackElement element = program.stack.pop();

        StackElement oldElement = program.stack.getFrameElement(immediate);

        if (oldElement.element2 instanceof Trackable) {
            ((Trackable)oldElement.element2).release();
        }

        program.stack.pushFrame(immediate, element);
    }
}
