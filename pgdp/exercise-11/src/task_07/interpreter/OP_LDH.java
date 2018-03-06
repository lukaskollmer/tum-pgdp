package task_07.interpreter;

import task_07.Util;

/**
 * LDH operation
 *
 * Pops two values off the stack (x1 and x2)
 * Then loads the value at offset x2 in the heap array referenced by x1 and pushes that onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(27)
@Operation.HasImmediate(false)
public class OP_LDH extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Heap.Slice slice = program.stack.pop().element2;
        int offset       = program.stack.pop().element1;

        if (slice == null) {
            throw new RuntimeException("trying to access array at invalid pointer");
        }

        program.stack.push(StackElement.withElement1(slice.get(offset)));
    }
}
