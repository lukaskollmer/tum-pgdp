package task_09.interpreter;

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
        Heap.Slice slice = (Heap.Slice) program.stack.pop();
        Integer offset   = (Integer) program.stack.pop();

        if (slice == null) {
            throw new RuntimeException("trying to access array at invalid pointer");
        }

        Object element = slice.get(offset);

        program.stack.push(element);
    }
}
