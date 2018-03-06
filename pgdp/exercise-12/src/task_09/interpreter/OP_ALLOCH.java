package task_09.interpreter;

import task_09.arc.Trackable;

/**
 * ALLOCH operation
 *
 * Pops one values off the stack (x1)
 * Then allocates an array of the size x1 on the heap, and pushes a reference to that array back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(29)
@Operation.HasImmediate(false)
public class OP_ALLOCH extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Integer size = (Integer) program.stack.pop();

        Heap.Slice slice = program.heap.allocate(size);

        program.stack.push(slice);

        Trackable.retainIfPossible(slice);

    }
}
