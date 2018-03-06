package task_09.interpreter;

/**
 * SHL operation
 *
 * Pops a value off the stack, shifts it by `immediate` bits to the left and pushes it back onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(26)
@Operation.HasImmediate(true)
public class OP_SHL extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        Integer value = (Integer) program.stack.pop();

        value <<= immediate;
        
        program.stack.push(value);
    }
}
