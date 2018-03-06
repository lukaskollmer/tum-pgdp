package task_07.interpreter;

import task_07.Util;

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
        int size = program.stack.pop().element1;

        Heap.Slice slice = program.heap.allocate(size);

        program.stack.push(StackElement.withElement2(slice));

        slice.retain();

    }
}
