package task_09.interpreter;

import task_09.arc.Trackable;

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
        Heap.Slice slice = (Heap.Slice) program.stack.pop();
        Integer offset   = (Integer) program.stack.pop();
        Object newValue  = program.stack.pop();

        Object oldValue = slice.get(offset);

        Trackable.releaseIfPossible(oldValue);
        Trackable.retainIfPossible(newValue);
        
        slice.set(offset, newValue);
    }
}
