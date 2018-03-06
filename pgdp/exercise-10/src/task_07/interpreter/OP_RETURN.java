package task_07.interpreter;

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
        int retval = program.stack.pop();

        for (int i = 0; i < immediate; i++) {
            program.stack.pop();
        }

        program.instructionPointer = program.stack.pop();
        program.stack.framePointer = program.stack.pop();

        program.stack.push(retval);
    }
}
