package task_07.interpreter;

import task_07.Util;

/**
 * STH operation
 *
 * Pops three values off the stack (x1, x2 and x3)
 * Then writes x3 into the heap-array referenced by x1, at offset x2
 * */
@SuppressWarnings("unused")
@Operation.Opcode(28)
@Operation.HasImmediate(false)
public class OP_STH extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Heap.Slice slice = program.stack.pop().element2;
        int offset       = program.stack.pop().element1;
        int newValue     = program.stack.pop().element1;
        
        slice.set(offset, newValue);
    }
}
